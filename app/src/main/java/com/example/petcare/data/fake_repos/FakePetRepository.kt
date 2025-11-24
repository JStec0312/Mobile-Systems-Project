package com.example.petcare.data.fake_repos

import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.config.DeveloperSettings
import com.example.petcare.data.dto.PetDto
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.datetime.LocalDate
import timber.log.Timber
import java.util.UUID

class FakePetRepository : IPetRepository {
    private val pets = mutableListOf<PetDto>();

    init{
        val pet1 = PetDto(
            id = DeveloperSettings.PET_1_ID,
            ownerUserId = DeveloperSettings.TEST_USER_ID,
            name = "Testy",
            species = speciesEnum.dog,
            breed = "Debugger Retriever",
            sex = sexEnum.male,
            birthDate = LocalDate(2020,1,1).toString(),
            avatarThumbUrl = DeveloperSettings.PET_1_THUMBNAIL,
            createdAt = DateConverter.localDateNow().toString()
        );
        val pet2 = PetDto(
            id = DeveloperSettings.PET_2_ID,
            ownerUserId = DeveloperSettings.TEST_USER_ID,
            name = "Buggy",
            species = speciesEnum.dog,
            breed = "Rhodesian Ridgebug",
            sex = sexEnum.male,
            birthDate = LocalDate(2021,6,15).toString(),
            avatarThumbUrl = DeveloperSettings.PET_2_THUMBNAIL,
            createdAt = DateConverter.localDateNow().toString()
        );
        pets.add(pet1)
        pets.add(pet2)
    }
    override suspend fun createPet(
        pet: Pet,
        avatarByteArray: ByteArray?
    ): PetDto {
        if (pet.name=="NetworkError"){
            throw Failure.NetworkError()
        }
        if (pet.name=="ServerError"){
            throw Failure.ServerError()
        }
        if (pet.name=="UnknownError"){
            throw Failure.UnknownError()
        }
        val petDto:PetDto = pet.toDto();
        pets.add(petDto);
        return petDto;
    }

    override suspend fun getPetById(petId: String): PetDto {
        val pet = pets.find { it.id == petId }
        if (petId=="NetworkError"){
            throw Failure.NetworkError()
        } else if (petId=="ServerError"){
            throw Failure.ServerError()
        } else if (petId=="UnknownError"){
            throw Failure.UnknownError()
        }
        return pet ?: throw GeneralFailure.PetNotFound()
    }

    override suspend fun deletePetById(petId: String, userId: String) {
        val petDto: PetDto? = pets.find { it.id == petId }
        if (petDto==null){
            throw GeneralFailure.PetNotFound()
        }
        if (petDto.ownerUserId!=userId){
            throw AuthFailure.PermissionDenied()
        }
        pets.removeIf { it.id == petId }
        return
    }

    override suspend fun getPetsByIds(petIds: List<String>): List<PetDto> {
        return pets.filter { petIds.contains(it.id) }
    }

    override suspend fun editPet(
        pet: Pet,
        avatarByteArray: ByteArray?
    ): PetDto {
        val petDto: PetDto = pet.toDto();
        val index = pets.indexOfFirst { it.id == petDto.id }
        if (index == -1) {
            throw GeneralFailure.PetNotFound()
        } else {
            pets[index] = petDto
            return petDto
        }
    }

}