package com.example.petcare.domain.use_case.add_pet

import com.example.petcare.common.Resource
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.model.PetMember
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import java.util.UUID
import javax.inject.Inject

class AddPetUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository,
    private val petMemberRepository: IPetMemberRepository,
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
        val userId: String?
        try {
            userId = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error("User not logged in"))
                return@flow
            }
        } catch (e: AuthFailure.UserNotLoggedIn){
            emit(Resource.Error("User not logged in"))
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
            createdAt = DateConverter.localDateNow()
        )
        try{
            petRepository.createPet( newPet, byteArrayImage)
            val petMember = PetMember(
                userId = userId,
                petId = newPetId,
                id = UUID.randomUUID().toString(),
                createdAt = DateConverter.localDateNow(),
            )
            petMemberRepository.addPetMember(
                petMember
            )
        } catch (e: Failure){
            emit(Resource.Error(e.message))
            return@flow
        }  catch (e: GeneralFailure.PetNotFound){
            emit(Resource.Error("Pet not found"))
            return@flow
        }
        emit(Resource.Success(Unit))
    }
}