package com.example.petcare.data.dto
import com.google.firebase.Timestamp
data class MedicationDto(
    val pet_id: String,                 // subkolekcja pod pets/{petId}/medications => pole opcjonalne
    val name: String,
    val form: String? = null,           // "tablet" | "syrup" | "drops" | "other"
    val dose: String? = null,           // np. "1 tab", "5 ml", "10 mg/kg"
    val notes: String? = null,
    val active: Boolean = true,
    val created_at: Timestamp? = null
)