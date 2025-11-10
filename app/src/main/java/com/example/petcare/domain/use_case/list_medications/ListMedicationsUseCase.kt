package com.example.petcare.domain.use_case.list_medications
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import javax.inject.Inject

class ListMedicationsUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository
){
    operator fun invoke(
        petId: UUID,
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail : Boolean = false, //@NOTE Simulated failure
    ): Flow<Resource<List<Medication>>> = flow {
        emit(Resource.Loading<List<Medication>>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error<List<Medication>>("Failed to get medications"))

        } else{
            val medications = listOf(
                Medication(
                    id = UUID.randomUUID(),
                    name = "Flea Treatment",
                    notes = "Ensure full coverage on the back",
                    petId = petProvider.getCurrentPetId(),
                    form = "Topical",
                    dose = "10ml",
                    active = true,
                    createdAt = Clock.System.now(),
                    from = Instant.parse("2023-10-01T10:00:00Z"),
                    to = Instant.parse("2024-10-01T10:00:00Z")
                ),
                Medication(
                    id = UUID.randomUUID(),
                    name = "Heartworm Prevention",
                    notes = "Administer monthly",
                    petId = petProvider.getCurrentPetId(),
                    form = "Oral",
                    dose = "1 tablet",
                    active = true,
                    createdAt = Clock.System.now(),
                    from = Instant.parse("2023-11-01T10:00:00Z"),
                    to = Instant.parse("2024-11-01T10:00:00Z")
                )
            )
            emit(Resource.Success<List<Medication>>(medications))
        }
    }

}