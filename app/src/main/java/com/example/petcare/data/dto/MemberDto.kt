package com.example.petcare.data.dto

import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.PetMember

// @NOTE The data classes were ai generated basing on the database schema

data class MemberDto (
    val id: String,
    val petId: String,
    val userId: String,
    val createdAt: String,
){
    fun toModel(): PetMember{
        return PetMember(
            id = this.id,
            petId = this.petId,
            userId = this.userId,
            createdAt = DateConverter.stringToLocalDate(createdAt)
        )
    }
}