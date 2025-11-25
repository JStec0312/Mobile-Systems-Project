package com.example.petcare.domain.use_case.list_medications
import com.example.petcare.common.Resource
import com.example.petcare.data.repository.MedicationRepository
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import javax.inject.Inject

class ListMedicationsUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val medicationRepository: IMedicationRepository
){
    operator fun invoke(
        petId: String,
    ): Flow<Resource<List<Medication>>> = flow {
        emit(Resource.Loading<List<Medication>>())
        try{
            val userId = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error<List<Medication>>("User not logged in"))
                return@flow
            }
            val medications = medicationRepository.listMedicationsForPet(petId)
            emit(Resource.Success<List<Medication>>(medications))
        } catch (e: Failure){
            emit(Resource.Error<List<Medication>>(e.message))
                return@flow
        }  catch (e: GeneralFailure.MedicationNotFound){
            emit(Resource.Error<List<Medication>>(e.message))
                return@flow
        }

    }

}