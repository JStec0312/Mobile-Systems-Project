package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.PetMemberDto
import com.example.petcare.domain.model.PetMember
import com.example.petcare.domain.repository.IPetMemberRepository

class FakePetMemberRepository: IPetMemberRepository {
    private val petMembers = ArrayList<PetMemberDto>()

    override fun addPetMember(petMemember: PetMember) {
        petMembers.add(petMemember.toDto())
    }

}