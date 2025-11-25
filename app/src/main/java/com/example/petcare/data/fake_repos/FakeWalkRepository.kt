package com.example.petcare.data.fake_repos

import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.Walk
import com.example.petcare.domain.repository.IWalkRepository

class FakeWalkRepository: IWalkRepository {
    private val walks = mutableListOf<Walk>();
    override fun createWalk(walk: Walk) {
        val walkDto = walk.toDto()
        walks.add(walk)
    }
}