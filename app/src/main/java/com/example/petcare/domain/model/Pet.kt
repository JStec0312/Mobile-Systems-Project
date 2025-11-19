package com.example.petcare.domain.model
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.data.dto.PetDto
import kotlinx.datetime.LocalDate

data class Pet(
    val id: String,
    val ownerUserId: String,
    val name: String,
    val species: speciesEnum,
    val breed: String?,
    val sex: sexEnum = sexEnum.unknown,
    val birthDate: LocalDate,
    val avatarThumbUrl: String?,
    val createdAt: LocalDate
) {
    fun toDto(): PetDto{
        return PetDto(
            id = this.id,
            ownerUserId = this.ownerUserId,
            name = this.name,
            species = this.species,
            breed = this.breed,
        )
    }
}
