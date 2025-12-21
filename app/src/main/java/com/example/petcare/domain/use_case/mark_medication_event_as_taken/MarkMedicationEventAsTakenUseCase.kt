package com.example.petcare.domain.use_case.mark_medication_event_as_taken

import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MarkMedicationEventAsTakenUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val medicationEventRepository: IMedicationEventRepository,
    private val petMemberRepository: IPetMemberRepository
) {
    operator fun invoke(
        medicationEventId: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val userId = userProvider.getUserId();
        if (userId == null) {
            emit(Resource.Error("User not logged in"));
            return@flow
        }
        val petId = petProvider.getCurrentPetId();
        if (petId == null) {
            emit(Resource.Error("No pet selected"));
            return@flow
        }
        if (!petMemberRepository.isUserPetMember(userId, petId)) {
            emit(Resource.Error("User does not have permission to mark medication event for this pet"))
            return@flow
        }
        try {
            medicationEventRepository.markMedicationEventAsTaken(medicationEventId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to mark medication event as taken: ${e.message}"))
        }
    }
}