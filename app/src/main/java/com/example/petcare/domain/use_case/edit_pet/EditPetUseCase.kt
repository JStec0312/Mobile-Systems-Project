package com.example.petcare.domain.use_case.edit_pet
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EditPetUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository
) {
    operator fun invoke(
        pet: Pet,
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail : Boolean = false, //@NOTE Simulated failure
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error<Unit>("Failed to edit pet"))

        } else{
            emit(Resource.Success<Unit>(Unit))
        }
    }
}