package com.example.petcare.domain.use_case.end_walk
import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IWalkRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import javax.inject.Inject

class EndWalkUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val walkRepository: IWalkRepository
){
    operator fun invoke(
        walkId: String,
        totalDistanceMeters: Float,
        totalSteps: Int,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try{
            walkRepository.setWalkAsEnded(
                walkId = walkId,
                totalDistanceMeters = totalDistanceMeters,
                totalSteps = totalSteps,
                endTime = Clock.System.now()
            )
        } catch (e: Failure){
            emit(Resource.Error(e.message))
        } catch(e: GeneralFailure){
            emit(Resource.Error(e.message))
        }
    }
}