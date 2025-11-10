package com.example.petcare.domain.use_case.add_pet_by_key

import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetShareCodeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddPetByKeyUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider ,
    private val petShareCodeRepository: IPetShareCodeRepository
) {
    operator fun invoke(
        petKey: String,
        delayMs: Long = 600,
        shouldFail: Boolean = false
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        delay(delayMs)

        if (shouldFail) {
            emit(Resource.Error("Failed to add pet by key"))
            return@flow
        }

        emit(Resource.Success(Unit))
    }
}
