package com.example.petcare.data.fake_repos

import com.example.petcare.common.medicationStatusEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.data.dto.fake.MedicationEventDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.model.MedicationEvent
import com.example.petcare.domain.repository.IMedicationEventRepository
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import java.util.UUID

class FakeMedicationEventRepository: IMedicationEventRepository {
    private val medicationEvents = mutableListOf<MedicationEventDto>();

    override suspend fun createByMedication(medication: Medication) {

        val startDateTime = DateTime(
            medication.from.year,
            medication.from.monthNumber - 1,
            medication.from.dayOfMonth
        )
        val endDate: LocalDate? = medication.to
        if (endDate == null) {
            return
        }
        val rule = RecurrenceRule(medication.reccurenceString)
        val iterator = rule.iterator(startDateTime)

        val times = medication.times

        while (iterator.hasNext()) {
            val nextInstance = iterator.next()
            val nextDate = LocalDate(
                nextInstance.year,
                nextInstance.month + 1,
                nextInstance.dayOfMonth
            )

            if (nextDate > endDate) break

            times.forEach { timeInstant ->

                val localTime = timeInstant
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .time

                val distinctDateTime = LocalDateTime(nextDate, localTime)

                val event = MedicationEventDto(
                    id = UUID.randomUUID().toString(),
                    medicationId = medication.id,
                    takenAt = null,
                    status = medicationStatusEnum.planned,
                    notes = null,
                    scheduledAt = distinctDateTime.toString(),
                    petId = medication.petId,
                    title = medication.name
                )
                medicationEvents.add(event)
            }
        }
    }

    override suspend fun getUpcomingMedicationEventsForUserInDateRange(
        petIds: List<String>,
        startDate: Instant,
        endDate: Instant
    ): List<MedicationEvent> {
        val upcomingEvents = medicationEvents.filter { event ->
            startDate <= DateConverter.stringToInstant(event.scheduledAt) &&  endDate >= DateConverter.stringToInstant(event.scheduledAt)
                    && petIds.contains(event.petId)
        }
        return upcomingEvents.map{evt -> evt.toDomain()}

    }



}