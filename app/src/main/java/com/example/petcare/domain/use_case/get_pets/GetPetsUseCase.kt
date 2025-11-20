package com.example.petcare.domain.use_case.get_pets

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPetsUseCase @Inject constructor(
    private val petRepository: IPetRepository,
    private val userProvider: IUserProvider,
) {
    operator fun invoke(): Flow<Resource<List<Pet>>> = flow {
        emit(Resource.Loading<List<Pet>>())
        val userId = userProvider.getUserId();
        if (userId == null){
            emit(Resource.Error<List<Pet>>("User not logged in"))
            return@flow
        }
        try {
            val petsDto = petRepository.getPets(userId)
            val pets = petsDto.map { it.toModel() }
            emit(Resource.Success<List<Pet>>(pets))
        } catch (e: Exception){
            emit(Resource.Error<List<Pet>>(e.message ?: "An unexpected error occurred"))
        }

    }
}