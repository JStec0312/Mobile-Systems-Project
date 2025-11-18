package com.example.petcare.domain.use_case.get_pet_by_id
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.data.dto.PetDto
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import javax.inject.Inject


class GetPetByIdUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository
) {
    operator fun invoke(
        petId: String,
    ): Flow<Resource<Pet>> = flow {
        emit(Resource.Loading<Pet>())
        try{
            var petDto: PetDto = petRepository.getPetById(petId)
            var pet = petDto.toModel();
            if (pet.ownerUserId != userProvider.getUserId()){
                emit(Resource.Error<Pet>("You are not the owner of this pet"))
            }
            petProvider.setCurrentPet(pet)
            emit(Resource.Success<Pet>(pet))
        } catch(e: Failure.NetworkError){
            emit(Resource.Error<Pet>(e.message))
        } catch (e: Failure.ServerError){
            emit(Resource.Error<Pet>(e.message))
        } catch (e: Failure.UnknownError){
            emit(Resource.Error<Pet>(e.message))
        }
    }
}