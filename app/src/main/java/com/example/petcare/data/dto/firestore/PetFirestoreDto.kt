package com.example.petcare.data.dto.firestore

import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.google.firebase.Timestamp

data class PetFirestoreDto (
    val id: String = "",
    val ownerUserId: String = "",
    val name: String = "",
    val species: speciesEnum = speciesEnum.dog,
    val breed: String? = null,
    val sex : sexEnum = sexEnum.unknown,
    val birthDate: Timestamp? = null,
    val avatarThumbUrl : String? = null,
    val createdAt: Timestamp? = null,
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_OWNER_USER_ID = "ownerUserId"
        const val FIELD_BIRTH_DATE = "birthDate"
        const val FIELD_CREATED_AT = "createdAt"
    }
}
