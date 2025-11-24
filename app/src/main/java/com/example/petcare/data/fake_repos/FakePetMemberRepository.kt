package com.example.petcare.data.fake_repos

import com.example.petcare.config.DeveloperSettings
import com.example.petcare.data.dto.PetMemberDto
import com.example.petcare.domain.model.PetMember
import com.example.petcare.domain.repository.IPetMemberRepository
import java.util.UUID

class FakePetMemberRepository: IPetMemberRepository {
    private val petMembers = ArrayList<PetMemberDto>()
    init{
        val testPetMember = PetMemberDto(
            petId = DeveloperSettings.PET_1_ID,
            userId = DeveloperSettings.TEST_USER_ID,
            id = UUID.randomUUID().toString(),
            createdAt = "2023-01-01T00:00:00Z",
        );
        val testPetMember2 = PetMemberDto(
            petId = DeveloperSettings.PET_2_ID,
            userId = DeveloperSettings.TEST_USER_ID,
            id = UUID.randomUUID().toString(),
            createdAt = "2023-01-01T00:00:00Z",
        )
        petMembers.add(testPetMember)
        petMembers.add(testPetMember2)
    }
    override fun addPetMember(petMemember: PetMember) {
        petMembers.add(petMemember.toDto())
    }

    override fun getPetIdsByUserId(userId: String): List<String> {
        return petMembers.filter { it.userId == userId }.map { it.petId }
    }

    override fun isUserPetMember(userId: String, petId: String): Boolean {
        return petMembers.any { it.userId == userId && it.petId == petId }
    }





}