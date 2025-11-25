package com.example.petcare.domain.use_case.delete_medication
import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationRepository
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
    private val medicationRepository: IMedicationRepository
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
            medicationRepository.deleteMedication(medicationId)
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