package com.example.petcare.data.dto

import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.Pet
import com.google.firebase.Timestamp

data class PetDto (
    val id: String,
    val ownerUserId: String,
    val name: String,
    val species: speciesEnum,
    val breed: String?,
    val sex : sexEnum = sexEnum.unknown,
    val birthDate: String? = null,
    val avatarThumbUrl : String? = null,
    val createdAt: String? = null

){
    public fun toModel(): Pet {
        return Pet(
            id = this.id ,
            ownerUserId = this.ownerUserId,
            name = this.name,
            species = this.species,
            breed = this.breed,
            sex = this.sex,
            birthDate = DateConverter.stringToLocalDate(this.birthDate),
            avatarThumbUrl = this.avatarThumbUrl,
            createdAt = DateConverter.stringToLocalDate(this.createdAt),
        )
    }
}