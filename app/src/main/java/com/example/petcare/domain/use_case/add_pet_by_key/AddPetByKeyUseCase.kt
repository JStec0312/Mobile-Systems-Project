package com.example.petcare.domain.use_case.add_pet_by_key

import com.example.petcare.common.Resource
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.PetMember
import com.example.petcare.domain.model.PetShareCode
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.IPetShareCodeRepository
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class AddPetByKeyUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider ,
    private val petShareCodeRepository: IPetShareCodeRepository,
    private val petMememberRepository: IPetMemberRepository
) {
    operator fun invoke(
        petKey: String,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val userId = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error("User not logged in"))
                return@flow
            }
            val petShareCode: PetShareCode? = petShareCodeRepository.getPetShareCodeByValue(shareCode = petKey)
            if (petShareCode == null){
                emit(Resource.Error("Invalid pet key"))
                return@flow
            }

            if (petShareCode.expiresAt < DateConverter.instantNow()){
                emit(Resource.Error("Pet key has expired"))
                return@flow
            }
            val petMember: PetMember = PetMember(
                id = UUID.randomUUID().toString(),
                petId = petShareCode.petId,
                userId = userId,
                createdAt = DateConverter.localDateNow(),
            )
            petMememberRepository.addPetMember(petMember)
            petShareCodeRepository.deletePetShareCodeById(petShareCode.id)
            emit(Resource.Success(Unit))
            return@flow
        } catch (e: Failure) {
            emit(Resource.Error(e.message))
            return@flow
        }
    }
}
