package com.example.petcare.domain.model

import com.example.petcare.data.dto.PetShareCodeDto
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class PetShareCode(
    val id: String,
    val petId: String,
    val code: String,
    val createdAt: LocalDate,
    val expiresAt: Instant,
) {
    fun toDto(): PetShareCodeDto{
        return PetShareCodeDto(
            id = this.id,
            petId = this.petId,
            code = this.code,
            expiresAt = this.expiresAt.toString(),
            createdAt = this.createdAt.toString()
        )
    }
}
