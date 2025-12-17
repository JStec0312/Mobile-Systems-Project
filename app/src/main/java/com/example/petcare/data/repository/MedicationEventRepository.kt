package com.example.petcare.data.repository

import com.example.petcare.common.utils.DateConverter
import com.example.petcare.data.dto.fake.MedicationEventDto
import com.example.petcare.data.dto.firestore.MedicationEventFirestoreDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.model.MedicationEvent
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.example.petcare.exceptions.Failure
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
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
                for (timeInstant in times){
                    val localTime = timeInstant
                        .toLocalDateTime(timeZone)
                        .time

                    val localDateTime = LocalDateTime(nextDate, localTime)
                    val scheduledInstant = localDateTime.toInstant(timeZone)
                    val scheduledMs = scheduledInstant.toEpochMilliseconds()
                    val docId = "${medication.id}_$scheduledMs"
                    val docRef = col.document(docId)
                    val exists = docRef.get().await().exists()
                    if (exists) {
                        continue
                    }
                    val dto = medication.toFirestoreDto();
                    batch.set(docRef, dto);
                    writes++;
                    if (writes == MAX_BATCH_WRITES){
                        batch.commit().await()
                        writes = 0;
                        batch = firestore.batch();
                    }
                }
            }
            if (writes > 0){
                batch.commit().await()
            }
        } catch (e: FirebaseFirestoreException){
            throw Failure.ServerError();
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
                    // jesli Twoj mapper bierze id z pola, OK; jak nie, mozesz przekazac doc.id
                    out.add(dto.toDomain())
                }
            }
        } catch (e: FirebaseFirestoreException) {
            throw e
        }

        return out.sortedBy { it.scheduledAt }
    }
}



private fun Instant.toFirebaseTimestamp(): Timestamp {
    val ms = this.toEpochMilliseconds()
    return Timestamp(ms / 1000, ((ms % 1000) * 1_000_000).toInt())
}
