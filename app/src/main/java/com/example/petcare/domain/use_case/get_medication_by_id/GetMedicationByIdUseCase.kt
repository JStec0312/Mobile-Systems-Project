package com.example.petcare.domain.use_case.get_medication_by_id

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMedicationByIdUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val medicationRepository: IMedicationRepository,
    private val medicationEventRepository: IMedicationEventRepository,
    private val petMemberRepository: IPetMemberRepository
    ) {
    operator fun invoke(
        medicationId: String,
    ): Flow<Resource<Medication>> = flow {
        try{
            emit(Resource.Loading());
            val userId: String? = userProvider.getUserId()
            if (userId==null){
                emit(Resource.Error("User not logged in"))
                return@flow
            }
            val petId = petProvider.getCurrentPetId()
            if (petId==null){
                emit(Resource.Error("No pet selected"))
                return@flow
            }
            if (!petMemberRepository.isUserPetMember(userId, petId)){
                emit(Resource.Error("User does not have permission to view this medication"))
                return@flow
            }
            val medication = medicationRepository.getMedicationById(medicationId)
            emit(Resource.Success(medication))
            return@flow
        } catch (e: Failure){
            emit(Resource.Error(e.message));
            return@flow
        }
    }
}