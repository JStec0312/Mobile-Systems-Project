package com.example.petcare.domain.remote

import com.example.petcare.domain.model.Pet

interface IVetAiGateway {
    suspend fun askVetAiQuestion(question: String, pet: Pet): String
}