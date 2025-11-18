package com.example.petcare.data.dto
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.PetShareCode


data class PetShareCodeDto(
    val id: String,
    val petId: String,
    val code: String,                 // np. "A1B2C3D4"
    val expiresAt: String? = null,
    val createdAt: String? = null,
) {
    fun toModel(): PetShareCode{
        return PetShareCode(
            id = this.id,
            petId = this.petId,
            code = this.code,
            createdAt = DateConverter.stringToLocalDate(createdAt)
        )
    }
}
