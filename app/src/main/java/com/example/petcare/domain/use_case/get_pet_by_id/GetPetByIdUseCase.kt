package com.example.petcare.domain.use_case.get_pet_by_id
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.model.sexEnum
import com.example.petcare.domain.model.speciesEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.*
import javax.inject.Inject


class GetPetByIdUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository
) {
    operator fun invoke(
        petId: UUID,
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail: Boolean = false, //@NOTE Simulated failure
    ): Flow<Resource<Pet>> = flow {
        emit(Resource.Loading<Pet>())
        delay(delayMs)
        if (shouldFail) {
            emit(Resource.Error("Failed to get pet"))
        } else {
            val example_pet = Pet(
                id = petId,
                ownerUserId = userProvider.getUserId(),
                name = "Fido",
                species = speciesEnum.dog,
                breed = "Golden Retriever",
                sex = sexEnum.male,
                birthDate = Clock.System.now(),
                avatarThumb_url = null,
                createdAt = Clock.System.now()
            );

            emit(Resource.Success<Pet>(example_pet))
        }
    }
}