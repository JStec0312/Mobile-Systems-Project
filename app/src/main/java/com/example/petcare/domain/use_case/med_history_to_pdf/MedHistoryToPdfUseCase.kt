package com.example.petcare.domain.use_case.med_history_to_pdf

import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationPdfGenerator
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class MedHistoryToPdfUseCase @Inject constructor(
    private val medicationRepository: IMedicationRepository,
    private val petMemberRepository: IPetMemberRepository,
    private val petProvider: IPetProvider,
    private  val pdf: IMedicationPdfGenerator,
    private val userProvider: IUserProvider
) {
    operator fun invoke(
        from: LocalDate?,
        to: LocalDate?,
    ): Flow<Resource<ByteArray>> = flow {
        val userId = userProvider.getUserId();
        if (userId == null){
            emit(Resource.Error("User not logged in"))
            return@flow
        }
        val petId = petProvider.getCurrentPetId();
        if (petId == null){
            emit(Resource.Error("No pet selected"))
            return@flow
        }
        if (!petMemberRepository.isUserPetMember(userId, petId)){
            emit(Resource.Error("User does not have permission to access this pet's medication history"))
            return@flow
        }

        if (from != null && to != null && from > to){
            emit(Resource.Error("Invalid date range: 'From' date is after 'To' date"))
            return@flow
        }
        try{
            var medications = medicationRepository.listMedicationsForPet(petId);
            if (from != null && to != null){
                medications = medications.filter { it.from >= from && it.to!! <= to}
            }

            val petName = petProvider.getCurrentPet().name;
            val pdfBytes = pdf.generateMedicationHistoryPdf(
                petName = petName,
                medications = medications
            )
            emit(Resource.Success(pdfBytes))

        } catch (e: Failure){
            emit(Resource.Error("An error occurred: ${e.message}"))
            return@flow
        }



    }

}