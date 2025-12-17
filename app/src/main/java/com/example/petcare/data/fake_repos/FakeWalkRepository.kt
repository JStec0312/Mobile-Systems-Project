package com.example.petcare.data.fake_repos

import com.example.petcare.common.utils.DateConverter
import com.example.petcare.data.dto.fake.WalkDto
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.Walk
import com.example.petcare.domain.repository.IWalkRepository
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.datetime.Instant

class FakeWalkRepository: IWalkRepository {
    private val walks = mutableListOf<WalkDto>();
    override fun createWalk(walk: Walk) {
        val walkDto = walk.toDto()
        walks.add(walk.toDto())
    }

    override fun setWalkAsEnded(
        walkId: String,
        totalDistanceMeters: Float,
        totalSteps: Int,
        endTime: Instant
    ) {
        val walkIndex = walks.indexOfFirst { it.id == walkId }
        if (walkIndex == -1) {
            throw GeneralFailure.WalkNotFound();
        }
        if (walks[walkIndex].endedAt != null) {
            throw GeneralFailure.WalkAlreadyEnded();
        }

        walks[walkIndex].endedAt = endTime.toString();
        val walkStartTime = DateConverter.stringToInstant(walks[walkIndex].startedAt);
        val durationSec = endTime.epochSeconds - walkStartTime.epochSeconds;
        walks[walkIndex].durationSec = durationSec.toInt();
        walks[walkIndex].distanceMeters = totalDistanceMeters.toInt();
        walks[walkIndex].pending= false;
        walks[walkIndex].steps = totalSteps;
        walks[walkIndex].endedAt = endTime.toString();


    }
}