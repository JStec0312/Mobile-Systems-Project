package com.example.petcare.domain.use_case.delete_medication
import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteMedicationUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val medicationRepository: IMedicationRepository,
    private val petMemberRepository: IPetMemberRepository,
    private val medicationEventRepository: IMedicationEventRepository
){
    operator fun invoke(
        medicationId: String,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        try{
            val userId = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error<Unit>("User not logged in"))
                return@flow
            }
            val petId = petProvider.getCurrentPetId();
            val medication = medicationRepository.getMedicationById(medicationId)
            if (!petMemberRepository.isUserPetMember(userId, medication.petId)){
                emit(Resource.Error<Unit>("User does not have permission to delete medication for this pet"))
                return@flow
            }
            medicationRepository.deleteMedication(medicationId)
            medicationEventRepository.deleteMedicationEventsForMedication(medicationId)

            emit(Resource.Success<Unit>(Unit))
        } catch (e: Failure){
            emit(Resource.Error<Unit>(e.message))
                return@flow
        } catch (e: GeneralFailure.MedicationNotFound){
            emit(Resource.Error<Unit>(e.message))
                return@flow
        }
    }
}