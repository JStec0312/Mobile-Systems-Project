package com.example.petcare.domain.use_case.add_pet

import com.example.petcare.common.Resource
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.providers.implementation.PetProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import java.util.UUID
import javax.inject.Inject

class AddPetUseCase @Inject constructor(
    private val userProvider: UserProvider,
    private val petProvider: PetProvider,
    private val petRepository: IPetRepository,
    private val userRepository: IUserRepository
) {
    operator fun invoke(
        name: String,
        species: speciesEnum,
        breed: String,
        birthDate: LocalDate,
        sex: sexEnum,
        byteArrayImage: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val userId = userProvider.getUserId()
        if (userId == null) {
            emit(Resource.Error("User not found"))
            return@flow
        }
        val newPetId : String = UUID.randomUUID().toString()
        val newPet = Pet(
            id = newPetId,
            ownerUserId = userId,
            name = name,
            species = species,
            breed = breed,
            sex = sex,
            birthDate = birthDate,
            avatarThumbUrl = null,
            createdAt = LocalDate(11, 12, 2003)
        )
        try{
            petRepository.createPet(userId, newPet, byteArrayImage)
        } catch (e: Failure.NetworkError){
            emit(Resource.Error("Network error"))
            return@flow
        } catch (e: Failure.ServerError){
            emit(Resource.Error("Server error"))
            return@flow
        } catch (e: Failure.UnknownError){
            emit(Resource.Error("Unknown error"))
            return@flow
        }
        emit(Resource.Success(Unit))
    }
}