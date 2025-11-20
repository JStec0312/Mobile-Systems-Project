package com.example.petcare.data.fake_repos

import com.example.petcare.data.dto.PetMemberDto
import com.example.petcare.domain.model.PetMember
import com.example.petcare.domain.repository.IPetMemberRepository
import kotlinx.datetime.LocalDate
import java.util.UUID

class FakePetMemberRepository: IPetMemberRepository {
    private val petMembers = ArrayList<PetMemberDto>()

    override fun addPetMember(petMember: PetMember) {
        petMembers.add(petMember.toDto())
    }

}