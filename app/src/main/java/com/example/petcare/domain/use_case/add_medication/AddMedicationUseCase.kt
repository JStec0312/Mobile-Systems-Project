package com.example.petcare.domain.use_case.add_medication
import com.example.petcare.common.Resource
import com.example.petcare.common.r
import com.example.petcare.domain.model.Medication
import kotlinx.coroutines.flow.Flow

class AddMedicationUseCase(
    private val delayMs: Long = 600,
    private val shouldFail : Boolean = false,
){
    operator fun invoke(): Flow<Resource<Medication>> = flow {
        emit()
    }
}