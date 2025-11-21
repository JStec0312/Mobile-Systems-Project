package com.example.petcare.data.fake_repos

import com.example.petcare.common.utils.DateConverter
import com.example.petcare.data.dto.PetShareCodeDto
import com.example.petcare.domain.model.PetShareCode
import com.example.petcare.domain.repository.IPetShareCodeRepository
import kotlinx.datetime.LocalDate

class FakePetShareCodeRepository: IPetShareCodeRepository {
    private val petShareCodes = mutableListOf<PetShareCodeDto>()

    override fun getPetShareCodeByValue(shareCode: String): PetShareCodeDto? {
        val petShareCode = petShareCodes.find { it.code == shareCode }
        return petShareCode;
    }

    override fun deletePetShareCodeById(shareCodeId: String) {
        petShareCodes.removeIf{it.id == shareCodeId}
    }

    override suspend fun createPetShareCode(petShareCode: PetShareCode): PetShareCodeDto {
        val petShareCodeDto:PetShareCodeDto = petShareCode.toDto();
        petShareCodes.add(petShareCodeDto);
        return petShareCodeDto;
    }
}