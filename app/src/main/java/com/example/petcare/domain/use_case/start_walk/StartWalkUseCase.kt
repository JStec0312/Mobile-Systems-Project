package com.example.petcare.domain.use_case.start_walk

import com.example.petcare.common.Resource
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.Walk
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.repository.IWalkRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class StartWalkUseCase @Inject constructor(
    private val userProvider: UserProvider,
    private val walkRepository: IWalkRepository,
    private val petProvider: IPetProvider
)  {
    operator fun invoke(): Flow<Resource<Walk>> = flow {
        emit(Resource.Loading())
        val walkId: String = UUID.randomUUID().toString();
        val petId = petProvider.getCurrentPetId()
        if (petId == null){
            emit(Resource.Error("No pet selected"))
            return@flow
        }
        try{
            val newWalk = Walk(
                id = walkId,
                petId = petId ,
                startedAt = DateConverter.localDateNow(),
                endedAt = null,
                durationSec = null,
                distanceMeters = null,
                steps = null,
                createdAt = DateConverter.localDateNow(),
                pending = true
            )
            walkRepository.createWalk(newWalk)
            emit(Resource.Success(newWalk))
        } catch (e: Failure){
            emit(Resource.Error(e.message))
            return@flow
        }
    }
}