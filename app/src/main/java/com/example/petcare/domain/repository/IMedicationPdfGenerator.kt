package com.example.petcare.domain.repository

interface IMedicationPdfGenerator {
    fun generateMedicationHistoryPdf(
        petName: String,
        medications: List<com.example.petcare.domain.model.Medication>
    ): ByteArray
}
