package com.example.petcare.data.dto
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.Medication

data class MedicationDto(
    val id: String,
    val petId: String,                 // subkolekcja pod pets/{petId}/medications => pole opcjonalne
    val name: String,
    val form: String? = null,           // "tablet" | "syrup" | "drops" | "other"
    val dose: String? = null,           // np. "1 tab", "5 ml", "10 mg/kg"
    val notes: String? = null,
    val active: Boolean = true,
    val createdAt: String,
    val from: String,
    val to: String,
) {
    fun toModel(): Medication{
        return Medication(
            petId = this.petId,
            name = this.name,
            form = this.form,
            dose = this.dose,
            notes = this.notes,
            active = this.active,
            createdAt = DateConverter.stringToLocalDate(this.createdAt),
            id = this.id,
            from = DateConverter.stringToLocalDate(this.from),
            to = DateConverter.stringToLocalDate(this.to),
        )
    }
}