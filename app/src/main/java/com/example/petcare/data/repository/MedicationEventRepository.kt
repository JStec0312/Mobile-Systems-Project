package com.example.petcare.data.repository

import com.example.petcare.common.medicationStatusEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.data.dto.fake.MedicationEventDto
import com.example.petcare.data.dto.firestore.MedicationEventFirestoreDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.model.MedicationEvent
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import javax.inject.Inject

class MedicationEventRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : IMedicationEventRepository {
    private val timeZone = TimeZone.currentSystemDefault();
    private val col = firestore.collection(FirestorePaths.MEDICATION_EVENTS)

    override suspend fun createByMedication(medication: Medication) {
        val endDate: LocalDate ? = medication.to
        if (endDate == null) {
            return
        }
        val startDateTime = DateTime(
            medication.from.year,
            medication.from.monthNumber - 1,
            medication.from.dayOfMonth
        )
        val rule = RecurrenceRule(medication.reccurenceString)
        val iterator = rule.iterator(startDateTime)
        val times = medication.times
        val MAX_BATCH_WRITES = 500
        var batch = firestore.batch()
        var writes = 0;
        try{
            while(iterator.hasNext()){
                val nextInstance = iterator.next()
                val nextDate = LocalDate(
                    nextInstance.year,
                    nextInstance.month + 1,
                    nextInstance.dayOfMonth
                );
                if (nextDate > endDate) break
                for (time in times){
                    val localDateTime = LocalDateTime(nextDate.year, nextDate.monthNumber, nextDate.dayOfMonth, time.hour, time.minute, time.second)
                    val scheduledInstant = localDateTime.toInstant(timeZone)
                    val scheduledMs = scheduledInstant.toEpochMilliseconds()
                    val docId = "${medication.id}_$scheduledMs"
                    val docRef = col.document(docId)
                    val exists = docRef.get().await().exists()
                    if (exists) {
                        continue
                    }
                    val dto = MedicationEventFirestoreDto(
                        id = docId,
                        petId = medication.petId,
                        medicationId = medication.id,
                        title = medication.name,
                        scheduledAt = scheduledInstant.toFirebaseTimestamp(),
                        takenAt = null,
                        status = medicationStatusEnum.planned,
                        notes = "",

                    )
                    batch.set(docRef, dto);
                    writes++;
                    if (writes >= MAX_BATCH_WRITES){
                        batch.commit().await()
                        writes = 0;
                        batch = firestore.batch();
                    }
                }
            }
            if (writes > 0){
                batch.commit().await()
            }
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "createByMedication");
        }
    }

    override suspend fun getUpcomingMedicationEventsForUserInDateRange(
        petIds: List<String>,
        startDate: Instant,
        endDate: Instant
    ): List<MedicationEvent> {
        if (petIds.isEmpty()) return emptyList()

        val startTs = startDate.toFirebaseTimestamp()
        val endTs = endDate.toFirebaseTimestamp()

        // whereIn limit 10 -> chunking
        val chunks = petIds.chunked(10)
        val out = mutableListOf<MedicationEvent>()

        try {
            for (chunk in chunks) {
                val snap = col
                    .whereIn(MedicationEventFirestoreDto.FIELD_PET_ID, chunk)
                    .whereGreaterThanOrEqualTo(MedicationEventFirestoreDto.FIELD_SCHEDULED_AT, startTs)
                    .whereLessThanOrEqualTo(MedicationEventFirestoreDto.FIELD_SCHEDULED_AT, endTs)
                    .orderBy(MedicationEventFirestoreDto.FIELD_SCHEDULED_AT)
                    .get()
                    .await()

                for (doc in snap.documents) {
                    val dto = doc.toObject(MedicationEventFirestoreDto::class.java) ?: continue
                    out.add(dto.toDomain())
                }
            }
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "getUpcomingMedicationEventsForUserInDateRange")
        }

        return out.sortedBy { it.scheduledAt }
    }

    override suspend fun markMedicationEventAsTaken(medicationEventId: String) {
        try {
            val docRef = col.document(medicationEventId)
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                throw GeneralFailure.MedicationNotFound("Medication event with id $medicationEventId not found")
            }
            val takenAt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
            docRef.update(
                mapOf(
                    MedicationEventFirestoreDto.FIELD_TAKEN_AT to takenAt.toFirebaseTimestamp(),
                    MedicationEventFirestoreDto.FIELD_TAKEN_AT to takenAt.toFirebaseTimestamp()
                )
            ).await()
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "markMedicationEventAsTaken")
        }
    }

    override suspend fun updateMedicationEventsForMedication(medication: Medication) {
        val endDate: LocalDate? = medication.to
        if (endDate == null) {
            return
        }

        val nowInstant = Clock.System.now()
        val nowDate = nowInstant.toLocalDateTime(timeZone).date
        val nowTs = nowInstant.toFirebaseTimestamp()

        try {
            var batch = firestore.batch()
            var writes = 0
            val MAX_BATCH_WRITES = 500

            val existingSnap = col
                .whereEqualTo(MedicationEventFirestoreDto.FIELD_MEDICATION_ID, medication.id)
                .whereGreaterThanOrEqualTo(MedicationEventFirestoreDto.FIELD_SCHEDULED_AT, nowTs)
                .get()
                .await()

            for (doc in existingSnap.documents) {
                batch.delete(doc.reference)
                writes++
                if (writes >= MAX_BATCH_WRITES) {
                    batch.commit().await()
                    batch = firestore.batch()
                    writes = 0
                }
            }
            if (writes > 0) {
                batch.commit().await()
            }
            if (endDate < nowDate) {
                return
            }
            val startDate = if (medication.from > nowDate) medication.from else nowDate

            val startDateTime = DateTime(
                startDate.year,
                startDate.monthNumber - 1,
                startDate.dayOfMonth
            )
            val rule = RecurrenceRule(medication.reccurenceString)
            val iterator = rule.iterator(startDateTime)
            val times = medication.times

            batch = firestore.batch()
            writes = 0

            while (iterator.hasNext()) {
                val nextInstance = iterator.next()
                val nextDate = LocalDate(
                    nextInstance.year,
                    nextInstance.month + 1,
                    nextInstance.dayOfMonth
                )

                if (nextDate > endDate) break

                for (time in times) {
                    val localDateTime = LocalDateTime(
                        nextDate.year,
                        nextDate.monthNumber,
                        nextDate.dayOfMonth,
                        time.hour,
                        time.minute,
                        time.second
                    )

                    val scheduledInstant = localDateTime.toInstant(timeZone)

                    // safety: jakby cos wyszlo przed "teraz", to pomijamy
                    if (scheduledInstant < nowInstant) continue

                    val scheduledMs = scheduledInstant.toEpochMilliseconds()
                    val docId = "${medication.id}_$scheduledMs"
                    val docRef = col.document(docId)

                    val dto = MedicationEventFirestoreDto(
                        id = docId,
                        petId = medication.petId,
                        medicationId = medication.id,
                        title = medication.name,
                        scheduledAt = scheduledInstant.toFirebaseTimestamp(),
                        takenAt = null,
                        status = medicationStatusEnum.planned,
                        notes = ""
                    )

                    batch.set(docRef, dto)
                    writes++
                    if (writes >= MAX_BATCH_WRITES) {
                        batch.commit().await()
                        batch = firestore.batch()
                        writes = 0
                    }
                }
            }

            if (writes > 0) {
                batch.commit().await()
            }

        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "updateMedicationEventsForMedication")
        }
    }
<<<<<<< Updated upstream

=======
<<<<<<< Updated upstream
=======

    override suspend fun deleteMedicationEventsForMedication(medicationId: String) {
        try {
            val existingSnap = col
                .whereEqualTo(MedicationEventFirestoreDto.FIELD_MEDICATION_ID, medicationId)
                .get()
                .await()

            val batch = firestore.batch()
            for (doc in existingSnap.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "deleteMedicationEventsForMedication")
        }
    }

>>>>>>> Stashed changes
>>>>>>> Stashed changes
}



fun Instant.toFirebaseTimestamp(): Timestamp {
    val ms = this.toEpochMilliseconds()
    return Timestamp(ms / 1000, ((ms % 1000) * 1_000_000).toInt())
}
