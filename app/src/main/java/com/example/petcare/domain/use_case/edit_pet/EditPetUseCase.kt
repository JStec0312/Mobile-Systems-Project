package com.example.petcare.domain.use_case.edit_pet
import com.example.petcare.common.Resource
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import timber.log.Timber
import javax.inject.Inject

class EditPetUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository
) {
    operator fun invoke(
        petId: String,
        ownerUserId : String,
        name: String?,
        species: speciesEnum?,
        breed: String?,
        sex: sexEnum?,
        birthDate: LocalDate?,
        avatarThumbUrl: String?,
        byteArrayImage: ByteArray?
    ): Flow<Resource<Pet>> = flow {
        emit(Resource.Loading())
        try{
            val petId = petId;
            val userId: String? = userProvider.getUserId();
            if (userId==null){
                emit(Resource.Error("User not logged in"))
                return@flow
            }

            Timber.tag("EditPetUseCase").d("Editing pet with ID: $petId, owner id: $ownerUserId by user: $userId")

            val petFromDb = petRepository.getPetById(petId)
            if (petFromDb.ownerUserId!= userId){
                Timber.tag("EditPetUseCase").d("User id: $userId is not owner of pet with id: $petId, owner id: $ownerUserId")
                emit(Resource.Error("You are not an owner of the pet"))
                return@flow
            }
            val pet = Pet(
                id = petId,
                ownerUserId = ownerUserId,
                name = name ?: petFromDb.name,
                species = species ?: petFromDb.species,
                breed = breed ?: petFromDb.breed,
                sex = sex ?: petFromDb.sex,
                birthDate = birthDate ?: petFromDb.birthDate,
                avatarThumbUrl = avatarThumbUrl ?: petFromDb.avatarThumbUrl,
                createdAt = petFromDb.createdAt,
            )
            val newPet: Pet = petRepository.editPet(pet, byteArrayImage)
            emit(Resource.Success(newPet))
            return@flow
        } catch (e: Failure){
            emit(Resource.Error(e.message))
            return@flow
        } catch(e: GeneralFailure.PetNotFound){
            emit(Resource.Error("Pet not found"))
            return@flow
        }
    }
}