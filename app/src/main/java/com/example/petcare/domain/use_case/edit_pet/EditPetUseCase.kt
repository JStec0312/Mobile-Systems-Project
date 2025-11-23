package com.example.petcare.domain.use_case.edit_pet
import com.example.petcare.common.Resource
import com.example.petcare.data.dto.PetDto
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EditPetUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository
) {
    operator fun invoke(
        pet: Pet,
        byteArrayImage: ByteArray?
    ): Flow<Resource<Pet>> = flow {
        emit(Resource.Loading())
        try{
            val petId = pet.id;
            val userId: String? = userProvider.getUserId();
            if (userId==null){
                emit(Resource.Error("User not logged in"))
                return@flow
            }
            if (petId==null){
                emit(Resource.Error("Pet ID is null"))
                return@flow
            }
            if (pet.ownerUserId != userId){
                emit(Resource.Error("You are not an owner of the pet"))
                return@flow
            }
            val newPetDto: PetDto = petRepository.editPet(pet, byteArrayImage);
            val newPet: Pet = newPetDto.toModel();
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