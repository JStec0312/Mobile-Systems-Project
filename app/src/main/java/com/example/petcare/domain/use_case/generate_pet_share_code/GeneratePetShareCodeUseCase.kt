package com.example.petcare.domain.use_case.generate_pet_share_code

import com.example.petcare.common.Resource
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.PetShareCode
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import com.example.petcare.domain.repository.IPetShareCodeRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class GeneratePetShareCodeUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository,
    private val petShareCodeRepository: IPetShareCodeRepository
) {
    operator fun invoke(
        petId: String,
    ): Flow<Resource<PetShareCode>> = flow {
        try{
            emit(Resource.Loading<PetShareCode>());
            val userId: String? = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error<PetShareCode>("User not logged in"));
                return@flow
            }
            val pet = petRepository.getPetById(petId);
            if (pet.ownerUserId != userId){
                emit(Resource.Error<PetShareCode>("You are not the owner of this pet"));
                return@flow
            }
            val petShareCode: PetShareCode = PetShareCode(
                id = UUID.randomUUID().toString(),
                petId = petId,
                code = UUID.randomUUID().toString().substring(0,8),
                createdAt = DateConverter.localDateNow(),
                expiresAt = DateConverter.setPetShareCodeExpiryDate(),
            )
            petShareCodeRepository.createPetShareCode(petShareCode);
            emit(Resource.Success<PetShareCode>(petShareCode));
            return@flow
        } catch (e: Failure){
            emit(Resource.Error<PetShareCode>(e.message))
            return@flow
        }

    }
}