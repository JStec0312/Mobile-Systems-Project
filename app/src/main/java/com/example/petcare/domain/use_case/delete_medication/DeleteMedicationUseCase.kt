package com.example.petcare.domain.use_case.delete_medication
import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationRepository
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
        medicationId: UUID,
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail : Boolean = false, //@NOTE Simulated failure
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error<Unit>("Failed to delete medication"))

        } else{
            emit(Resource.Success<Unit>(Unit))
        }
    }
}