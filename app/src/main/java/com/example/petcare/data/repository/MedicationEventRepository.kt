package com.example.petcare.data.repository

import com.example.petcare.common.utils.DateConverter
import com.example.petcare.data.dto.fake.MedicationEventDto
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.model.MedicationEvent
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
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

    override suspend fun createByMedication(medication: Medication) {
//        val endDate = medication.to ?: return
//        val horizon = DateConverter.localDateNow().plus(DatePeriod(days = 90))
//        val effectiveEnd = minOf(endDate, horizon);
//        val startDateTime = DateTime(
//            medication.from.year,
//            medication.from.monthNumber - 1,
//            medication.from.dayOfMonth
//        )
//        val rule = RecurrenceRule(medication.reccurenceString);
//        val iterator = rule.iterator(startDateTime)
//        val times = medication.times
//        val col = firestore.collection(FirestorePaths.MEDICATION_EVENTS)
//        var batch = firestore.batch()
//        var ops = 0;
//        while (iterator.hasNext()) {
//            val nextInstance = iterator.next()
//            val nextDate = LocalDate(nextInstance.year, nextInstance.month + 1, nextInstance.dayOfMonth)
//
//            if (nextDate > effectiveEnd) break
//
//            for (timeInstant in times) {
//                val localTime = timeInstant.toLocalDateTime(timeZone).time
//                val localDateTime = LocalDateTime(nextDate, localTime)
//                val scheduledInstant = localDateTime.toInstant(timeZone)
//                val scheduledMs = scheduledInstant.toEpochMilliseconds()
//                val docId = "${medication.id}_$scheduledMs"
//
//                val dto = MedicationEventDto(
//                    petId = medication.petId,
//                    medicationId = medication.id,
//                    title = medication.name,
//                    scheduledAt = Timestamp(scheduledMs / 1000, ((scheduledMs % 1000) * 1_000_000).toInt()),
//                    takenAt = null,
//                    status = medicationStatusEnum.planned.name,
//                    notes = null
//                )
//
//                val docRef = col.document(docId)
//                batch.set(docRef, dto, SetOptions.merge())
//                ops++
//
//                if (ops == 450) { // zostaw margines, nie cisnij pod korek
//                    batch.commit().await()
//                    batch = firestore.batch()
//                    ops = 0
//                }
//            }
//        }
//
//        if (ops > 0) batch.commit().await()
    }


    override suspend fun getUpcomingMedicationEventsForUserInDateRange(
        petIds: List<String>,
        startDate: Instant,
        endDate: Instant
    ): List<MedicationEvent> {
        TODO("Not yet implemented")
    }
}