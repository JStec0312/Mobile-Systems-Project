package com.example.petcare.data.dto.fake

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
    val reccurenceString: String?,
    val times: List<String>
)
