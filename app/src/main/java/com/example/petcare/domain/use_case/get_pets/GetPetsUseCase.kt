package com.example.petcare.domain.use_case.get_pets

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class GetPetsUseCase @Inject constructor(
    private val petRepository: IPetRepository,
    private val userProvider: IUserProvider,
    private val petMemberRepository: IPetMemberRepository
) {
    operator fun invoke(): Flow<Resource<List<Pet>>> = flow {
        emit(Resource.Loading<List<Pet>>())
        Timber.d("GetPetsUseCase invoked")
        try {
            val userId: String? = userProvider.getUserId();
            Timber.d("Logged in userId: $userId")
            if (userId == null){
                emit(Resource.Error<List<Pet>>("User not logged in"));
                return@flow
            }
            val petIds = petMemberRepository.getPetIdsByUserId(userId)
            val pets = petRepository.getPetsByIds(petIds)

            Timber.d("Pets: $pets")
            emit(Resource.Success<List<Pet>>(pets))
            return@flow
        } catch(e: Failure){
            emit(Resource.Error<List<Pet>>(e.message))
            return@flow
        }

    }
}