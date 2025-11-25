package com.example.petcare.domain.model
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.data.dto.MedicationDto
import kotlinx.datetime.LocalDate

data class Medication(
    val id: String,
    val petId: String,
    val name: String,
    val form: String?,
    val dose: String?,
    val notes: String?,
    val active: Boolean = true,
    val createdAt: LocalDate,
    val from: LocalDate,
    val to: LocalDate?
) {
    fun toDto(): MedicationDto {
        return MedicationDto(
            id = this.id,
            petId = this.petId,
            name = this.name,
            form = this.form,
            dose = this.dose,
            notes = this.notes,
            active = this.active,
            createdAt = DateConverter.localDateToString(this.createdAt),
            from = DateConverter.localDateToString(this.from),
            to = DateConverter.localDateToString(this.to),
        )
    }
}