package com.example.petcare.domain.use_case.get_pet_by_key

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.domain.repository.IPetShareCodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPetByKeyUseCase @Inject constructor(
    private val shareCodeRepository: IPetShareCodeRepository,
    private val petRepository: IPetRepository
) {
    operator fun invoke(code: String): Flow<Resource<Pet>> = flow {
        emit(Resource.Loading())
        try {
            val shareCode = shareCodeRepository.getPetShareCodeByValue(code.trim())

            if(shareCode == null) {
                emit(Resource.Error("Invalid or expired share code."))
                return@flow
            }
            val pet = petRepository.getPetById(shareCode.petId)
            emit(Resource.Success(pet))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred."))
        }
    }
}