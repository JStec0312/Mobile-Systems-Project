package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.PetShareCodeDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.PetShareCode
import com.example.petcare.domain.repository.IPetShareCodeRepository

class FakePetShareCodeRepository: IPetShareCodeRepository {
    private val petShareCodes = mutableListOf<PetShareCodeDto>()

    override fun getPetShareCodeByValue(shareCode: String): PetShareCode? {
        val petShareCode = petShareCodes.find { it.code == shareCode }
        return petShareCode?.toDomain()
    }

    override fun deletePetShareCodeById(shareCodeId: String) {
        petShareCodes.removeIf { it.id == shareCodeId }
    }

    override suspend fun createPetShareCode(petShareCode: PetShareCode): PetShareCode {
        val petShareCodeDto: PetShareCodeDto = petShareCode.toDto()
        petShareCodes.add(petShareCodeDto)
        return petShareCodeDto.toDomain()
    }
}