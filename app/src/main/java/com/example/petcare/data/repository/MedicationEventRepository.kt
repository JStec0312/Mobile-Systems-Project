package com.example.petcare.data.repository

import com.example.petcare.common.medicationStatusEnum
import com.example.petcare.data.dto.MedicationEventDto
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.repository.IMedicationEventRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.dmfs.rfc5545.DateTime
import java.util.UUID

class MedicationEventRepository: IMedicationEventRepository {
    private val medicationEvents = mutableListOf<MedicationEventDto>();
    override fun createByMedication(
        medication: Medication,
    ) {
        val startDateTime = DateTime(medication.from.year, medication.from.monthNumber - 1 , medication.from.dayOfMonth)
        val rule = RecurrenceRule(medication.reccurenceString);
        val iterator = rule.iterator(startDateTime);
        val times = medication.times;
        while (iterator.hasNext()){
            val nextInstance = iterator.next();
            val nextDate = LocalDate(nextInstance.year, nextInstance.month + 1, nextInstance.dayOfMonth);
            times.forEach { time ->
                val distincDateTime = LocalDateTime(nextDate, time);
                val event = MedicationEventDto(
                    id = UUID.randomUUID().toString(),
                    medicationId = medication.id,
                    takenAt = null,
                    status = medicationStatusEnum.planned,
                    notes = null,
                    scheduledAt = distincDateTime.toString(),
                )
                
            }
        }
    }


}