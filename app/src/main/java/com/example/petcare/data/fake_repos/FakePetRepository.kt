package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.PetDto
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.GeneralFailure

class FakePetRepository : IPetRepository {
    private val pets = mutableListOf<PetDto>();
    override suspend fun createPet(
        pet: Pet,
        avatarByteArray: ByteArray?
    ): PetDto {
        if (pet.name=="NetworkError"){
            throw com.example.petcare.exceptions.Failure.NetworkError()
        }
        if (pet.name=="ServerError"){
            throw com.example.petcare.exceptions.Failure.ServerError()
        }
        if (pet.name=="UnknownError"){
            throw com.example.petcare.exceptions.Failure.UnknownError()
        }
        val petDto:PetDto = pet.toDto();
        pets.add(petDto);
        return petDto;
    }

    override suspend fun getPetById(petId: String): PetDto {
        val pet = pets.find { it.id == petId }
        if (petId=="NetworkError"){
            throw com.example.petcare.exceptions.Failure.NetworkError()
        } else if (petId=="ServerError"){
            throw com.example.petcare.exceptions.Failure.ServerError()
        } else if (petId=="UnknownError"){
            throw com.example.petcare.exceptions.Failure.UnknownError()
        }
        return pet ?: throw com.example.petcare.exceptions.GeneralFailure.PetNotFound()
    }

    override suspend fun deletePetById(petId: String, userId: String) {
        val petDto: PetDto? = pets.find { it.id == petId }
        if (petDto==null){
            throw GeneralFailure.PetNotFound()
        }
        if (petDto.ownerUserId!=userId){
            throw com.example.petcare.exceptions.AuthFailure.PermissionDenied()
        }
        pets.removeIf { it.id == petId }
        return
    }
    override suspend fun getPetsByUserId(userId: String): List<PetDto> {
        if (userId=="NetworkError"){
            throw com.example.petcare.exceptions.Failure.NetworkError()
        } else if (userId=="ServerError"){
            throw com.example.petcare.exceptions.Failure.ServerError()
        } else if (userId=="UnknownError"){
            throw com.example.petcare.exceptions.Failure.UnknownError()
        }
        return pets.filter { it.ownerUserId == userId }
    }

}