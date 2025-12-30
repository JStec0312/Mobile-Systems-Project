package com.example.petcare.domain.use_case.edit_medication_use_case

import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class EditMedicationUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val medicationRepository: IMedicationRepository,
    private val medicationEventRepository: IMedicationEventRepository,
    private val petMemberRepository: IPetMemberRepository
) {
    operator fun invoke(
        medicationId: String,
        newName: String?,
        newForm: String?,
        newDose: String?,
        newNotes: String?,
        newFrom: LocalDate,
        newTo: LocalDate?,
        newTimes: List<kotlinx.datetime.LocalTime>
    ) : Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try{
            val userId = userProvider.getUserId()
            if(userId == null){
                emit(Resource.Error("User not logged in"))
                return@flow
            }
            val petId = petProvider.getCurrentPetId()
            if(petId == null){
                emit(Resource.Error("No pet selected"))
                return@flow
            }
            if (!petMemberRepository.isUserPetMember(userId, petId)) {
                emit(Resource.Error("User does not have permission to edit medication for this pet"))
                return@flow
            }
            val medication = medicationRepository.getMedicationById(medicationId)
            if(medication == null){
                emit(Resource.Error("Medication not found"))
                return@flow
            }
            val currentFrom = newFrom ?: medication.from
            val currentTo = newTo ?: medication.to
            if (currentTo != null && currentFrom > currentTo) {
                emit(Resource.Error("Invalid date range: 'from' date is after 'to' date"))
                return@flow
            }
            val updatedMedication = medication.copy(
                name = newName ?: medication.name,
                form = newForm ?: medication.form,
                dose = newDose ?: medication.dose,
                notes = newNotes ?: medication.notes,
                from = currentFrom,
                to = currentTo,
                times = if(newTimes.isNotEmpty()) newTimes else medication.times
            )
            medicationRepository.updateMedication(updatedMedication)
            medicationEventRepository.updateMedicationEventsForMedication(updatedMedication)
            emit(Resource.Success(Unit))
        } catch (e: Failure){
            emit(Resource.Error("An error occurred: ${e.message}"))
        }
    }
}