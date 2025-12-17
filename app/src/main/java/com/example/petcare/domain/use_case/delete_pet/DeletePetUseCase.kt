package com.example.petcare.domain.use_case.delete_pet
import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeletePetUseCase  @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository,
    private val petMemberRepostiory: IPetMemberRepository
){
    operator fun invoke(
        petId: String,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        try{
            val userId: String? = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error<Unit>("User not logged in"))
                return@flow
            }
            val pet = petRepository.getPetById(petId);
            if (pet == null){
                emit(Resource.Error<Unit>("Pet not found"))
                return@flow
            }
            if (pet.ownerUserId != userId){
                emit(Resource.Error<Unit>("You are not an owner of this pet"))
                return@flow
            }

            petRepository.deletePetById(
                petId = petId,
                userId = userId
            );
            petProvider.setCurrentPetId(null);
            emit(Resource.Success<Unit>(Unit))
            return@flow
        } catch(e: Failure.NetworkError){
            emit(Resource.Error<Unit>("Network error occurred"))
            return@flow
        } catch (e: Failure.ServerError){
            emit(Resource.Error<Unit>("Server error occurred"))
            return@flow
        } catch (e: Failure.UnknownError){
            emit(Resource.Error<Unit>("Unknown error occurred"))
            return@flow
        } catch (e: AuthFailure.PermissionDenied){
            emit(Resource.Error<Unit>("You are not an owner of this pet"))
            return@flow
        } catch(e: GeneralFailure.PetNotFound){
            emit(Resource.Error<Unit>("Pet not found"))
            return@flow
        }

    }

}