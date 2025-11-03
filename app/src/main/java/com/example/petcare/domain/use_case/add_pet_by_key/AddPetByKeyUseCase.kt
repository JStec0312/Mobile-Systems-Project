package com.example.petcare.domain.use_case.add_pet_by_key

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.model.sexEnum
import com.example.petcare.domain.model.speciesEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class AddPetByKeyUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider
) {
    suspend operator fun invoke(
        petKey: String,
        delayMs: Long = 600,
        shouldFail: Boolean = false
    ): Flow<Resource<Pet>> = flow {
        emit(Resource.Loading<Pet>());
        delay(delayMs);

        if (shouldFail) {
            emit(Resource.Error<Pet>("Failed to add pet by key"))
        } else {
            val pet = Pet(
                id = UUID.randomUUID(),
                ownerUserId = UUID.randomUUID(),
                name = "mock pet",
                species = speciesEnum.dog,
                breedId = UUID.randomUUID(),
                sex = sexEnum.unknown,
                birthDate = Clock.System.now(),
                avatarThumb_url = null,
                createdAt = Clock.System.now()
            )
            emit(Resource.Success<Pet>(pet))
        }
    }
}