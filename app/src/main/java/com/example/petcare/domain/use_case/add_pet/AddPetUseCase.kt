package com.example.petcare.domain.use_case.add_pet

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.model.sexEnum
import com.example.petcare.domain.model.speciesEnum
import com.example.petcare.domain.providers.implementation.PetProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.repository.IPetRepository
import kotlinx.datetime.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class AddPetUseCase @Inject constructor(
    private val userProvider: UserProvider,
    private val petProvider: PetProvider,
    private val petRepository: IPetRepository
) {
    operator fun invoke(
        name: String,
        species: speciesEnum,
        breed: String,
        birthDate: Instant,
        breedId: UUID,
        sex: sexEnum,
        delayms: Long = 500,
        shouldFail: Boolean = false,
        byteArrayImage: ByteArray?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        delay(delayms)

        if (shouldFail) {
            emit(Resource.Error<Unit>("Failed to add pet"))
            return@flow
        }

        emit(Resource.Success(Unit))
    }
}