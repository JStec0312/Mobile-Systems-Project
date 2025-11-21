package com.example.petcare.domain.use_case.add_medication
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationRepository
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject


class AddMedicationUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val medicationRepository: IMedicationRepository
){
    operator fun invoke(
        petId: UUID,
        name: String,
        form: String?,
        dose: String?,
        notes: String?,
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail : Boolean = false, //@NOTE Simulated failure
    ): Flow<Resource<Medication>> = flow {
        emit(Resource.Loading<Medication>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error<Medication>("Failed to add medication"))

        } else{
            val medication = Medication(
                id = UUID.randomUUID().toString(),
                petId = UUID.randomUUID().toString(),
                name = "Mock",
                form = "Mock",
                dose = "Mock dose",
                notes = "Mock notes",
                createdAt = LocalDate(2023,1,1),
                active = true,
                from = LocalDate(2023,1,1),
                to = LocalDate(2023, 12, 31),
            )
            emit(Resource.Success<Medication>(medication))
        }
    }
}