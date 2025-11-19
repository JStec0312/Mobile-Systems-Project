package com.example.petcare.domain.model

import com.example.petcare.data.dto.PetMemberDto
import kotlinx.datetime.LocalDate

data class PetMember(
    val id: String,
    val petId: String,
    val userId: String,
    val createdAt: LocalDate
) {
    fun toDto(): PetMemberDto{
        return PetMemberDto(
            id = this.id,
            petId = this.petId,
            userId = this.userId,
            createdAt = this.createdAt.toString()
        )
    }
}