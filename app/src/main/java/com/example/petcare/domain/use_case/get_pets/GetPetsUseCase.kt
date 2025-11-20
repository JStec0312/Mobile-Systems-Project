package com.example.petcare.domain.use_case.get_pets

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPetsUseCase @Inject constructor(
    private val petRepository: IPetRepository,
    private val userProvider: IUserProvider,
) {
    suspend operator fun invoke(): Flow<Resource<List<Pet>>> = flow {
        emit(Resource.Loading<List<Pet>>())
        try {
            val userId = userProvider.getUserId();
            val petsDto = petRepository.getPetsByUserId(userId)
            val pets = petsDto.map { it.toModel() }
            emit(Resource.Success<List<Pet>>(pets))
            return@flow
        } catch(e: Failure){
            emit(Resource.Error<List<Pet>>(e.message))
            return@flow
        }

    }
}