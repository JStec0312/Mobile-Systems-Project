package com.example.petcare.data.dto
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.Walk

data class WalkDto(
    val id: String,
    val petId: String,                  // subkolekcja pod pets/{petId}/walks => pole opcjonalne
    val startedAt: String,
    val endedAt: String? = null,
    val durationSec: Int? = null,
    val distanceMeters: Int? = null,
    val steps: Int? = null,
    val pending: Boolean,
    val createdAt: String,
){
    fun toModel(): Walk{
        return Walk(
            id = this.id,
            petId = this.petId,
            startedAt = DateConverter.stringToLocalDate(this.startedAt),
            endedAt = DateConverter.stringToLocalDate(this.endedAt),
            durationSec = durationSec,
            distanceMeters = this.distanceMeters,
            steps = this.steps,
            pending = this.pending,
            createdAt = DateConverter.stringToLocalDate(this.createdAt),
        )
    }
}

