package com.example.petcare.data.mapper

import com.example.petcare.common.utils.DateConverter
import com.example.petcare.data.dto.*
import com.example.petcare.domain.model.*

// Extension functions for mapping DTOs to Domain models
fun NotificationSettingDto.toDomain(): NotificationSettings {
    return NotificationSettings(
        id = this.id,
        userId = this.userId,
        category = this.category,
        createdAt = DateConverter.stringToLocalDate(this.createdAt),
        enabled = this.enabled
    )
}

fun PetDto.toDomain(): Pet {
    return Pet(
        id = this.id,
        ownerUserId = this.ownerUserId,
        name = this.name,
        species = this.species,
        breed = this.breed,
        sex = this.sex,
        birthDate = DateConverter.stringToLocalDate(this.birthDate),
        avatarThumbUrl = this.avatarThumbUrl,
        createdAt = DateConverter.stringToLocalDate(this.createdAt)
    )
}

fun UserDto.toDomain(): User {
    return User(
        email = this.email,
        displayName = this.displayName,
        id = this.id
    )
}

fun TaskDto.toDomain(): Task {
    return Task(
        id = this.id,
        petId = this.petId,
        type = this.type,
        title = this.title,
        notes = this.notes,
        priority = this.priority,
        status = this.status,
        createdAt = DateConverter.stringToLocalDate(this.createdAt),
        date = DateConverter.stringToInstant(this.date)
    )
}

fun PetMemberDto.toDomain(): PetMember {
    return PetMember(
        id = this.id,
        petId = this.petId,
        userId = this.userId,
        createdAt = DateConverter.stringToLocalDate(this.createdAt)
    )
}

fun MedicationDto.toDomain(): Medication {
    return Medication(
        id = this.id,
        petId = this.petId,
        name = this.name,
        form = this.form,
        dose = this.dose,
        notes = this.notes,
        active = this.active,
        createdAt = DateConverter.stringToLocalDate(this.createdAt),
        from = DateConverter.stringToLocalDate(this.from),
        to = DateConverter.stringToLocalDate(this.to),
        reccurenceString = this.reccurenceString,
        times = this.times.map { it ->  DateConverter.stringToLocalTime(it)}
    )
}

// Extension functions for mapping Domain models to DTOs
fun NotificationSettings.toDto(): NotificationSettingDto {
    return NotificationSettingDto(
        id = this.id,
        userId = this.userId,
        category = this.category,
        createdAt = this.createdAt.toString(),
        enabled = this.enabled,
    )
}

fun Pet.toDto(): PetDto {
    return PetDto(
        id = this.id,
        ownerUserId = this.ownerUserId,
        name = this.name,
        species = this.species,
        breed = this.breed,
        sex = this.sex,
        birthDate = this.birthDate.toString(),
        avatarThumbUrl = this.avatarThumbUrl,
        createdAt = this.createdAt.toString()
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        email = this.email,
        displayName = this.displayName
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = this.id,
        petId = this.petId,
        type = this.type,
        title = this.title,
        notes = this.notes,
        priority = this.priority,
        status = this.status,
        createdAt = this.createdAt.toString(),
        date = this.date.toString()
    )
}

fun PetMember.toDto(): PetMemberDto {
    return PetMemberDto(
        id = this.id,
        petId = this.petId,
        userId = this.userId,
        createdAt = this.createdAt.toString()
    )
}

fun Medication.toDto(): MedicationDto {
    return MedicationDto(
        id = this.id,
        petId = this.petId,
        name = this.name,
        form = this.form,
        dose = this.dose,
        notes = this.notes,
        active = this.active,
        createdAt = DateConverter.localDateToString(this.createdAt),
        from = DateConverter.localDateToString(this.from),
        to = DateConverter.localDateToString(this.to),
        reccurenceString = this.reccurenceString,
        times = this.times.map { it.toString() }
    )
}

fun WalkDto.toDomain(): Walk {
    return Walk(
        id = this.id,
        petId = this.petId,
        startedAt = DateConverter.stringToLocalDate(this.startedAt),
        endedAt = DateConverter.stringToLocalDate(this.endedAt),
        durationSec = this.durationSec,
        distanceMeters = this.distanceMeters,
        steps = this.steps,
        pending = this.pending,
        createdAt = DateConverter.stringToLocalDate(this.createdAt)
    )
}

fun Walk.toDto(): WalkDto {
    return WalkDto(
        id = this.id,
        petId = this.petId,
        startedAt = this.startedAt.toString(),
        endedAt = this.endedAt?.toString(),
        durationSec = this.durationSec,
        distanceMeters = this.distanceMeters,
        steps = this.steps,
        pending = this.pending,
        createdAt = this.createdAt.toString()
    )
}

fun WalkTrackPointDto.toDomain(): WalkTrackPoint {
    return WalkTrackPoint(
        id = this.id,
        walkId = this.walkId,
        ts = this.ts,
        lat = this.lat,
        lon = this.lon,
    )
}

fun WalkTrackPoint.toDto(): WalkTrackPointDto {
    return WalkTrackPointDto(
        id = this.id,
        walkId = this.walkId,
        ts = this.ts,
        lat = this.lat,
        lon = this.lon,
    )
}

fun PetShareCodeDto.toDomain(): PetShareCode {
    return PetShareCode(
        id = this.id,
        petId = this.petId,
        code = this.code,
        createdAt = DateConverter.stringToLocalDate(this.createdAt),
        expiresAt = DateConverter.stringToInstant(this.expiresAt)
    )
}

fun PetShareCode.toDto(): PetShareCodeDto {
    return PetShareCodeDto(
        id = this.id,
        petId = this.petId,
        code = this.code,
        expiresAt = this.expiresAt.toString(),
        createdAt = this.createdAt.toString()
    )
}




fun MedicationEventDto.toDomain(): MedicationEvent {
    return MedicationEvent(
        id = this.id,
        medicationId = this.medicationId,
        takenAt = DateConverter.stringToLocalDate(this.takenAt),
        status = this.status,
        notes = this.notes,
        scheduledAt = DateConverter.stringToLocalDate(this.scheduledAt!!),
    )
}

fun MedicationEvent.toDto(): MedicationEventDto {
    return MedicationEventDto(
        id = this.id,
        medicationId = this.medicationId,
        takenAt = this.takenAt.toString(),
        status = this.status,
        notes = this.notes,
        scheduledAt = this.scheduledAt.toString(),
    )
}

