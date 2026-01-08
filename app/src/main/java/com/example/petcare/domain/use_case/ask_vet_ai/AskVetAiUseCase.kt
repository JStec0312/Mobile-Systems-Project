package com.example.petcare.domain.use_case.ask_vet_ai

import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.remote.IVetAiGateway
import com.example.petcare.domain.repository.IPetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AskVetAiUseCase @Inject constructor(
    private val vetAiGateway: IVetAiGateway,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository
) {
    operator fun invoke(question: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val petId = petProvider.getCurrentPetId()
            if (petId == null) {
                emit(Resource.Error("No pet selected"))
                return@flow
            }
            val pet = petRepository.getPetById(petId)
            val answer = vetAiGateway.askVetAiQuestion(question, pet)

            emit(Resource.Success(answer))
        } catch (e: Exception) {
            emit(Resource.Error("An error occurred: ${e.message}"))
        }
    }

}