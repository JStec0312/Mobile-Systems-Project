package com.example.petcare.data.mapper

import com.example.petcare.common.taskTypeEnum
import com.example.petcare.data.dto.firestore.*
import com.example.petcare.domain.model.*
import com.google.firebase.Timestamp
import kotlinx.datetime.*
import java.util.Date
import java.time.ZoneId

// Helper extensions for converting between com.google.firebase.Timestamp and kotlinx.datetime types
private fun Timestamp.toKotlinInstant(): Instant = Instant.fromEpochMilliseconds(this.toDate().time)
private fun Timestamp?.toKotlinInstantOrNull(): Instant? = this?.toKotlinInstant()

private fun Timestamp.toKotlinLocalDate(systemZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    this.toKotlinInstant().toLocalDateTime(systemZone).date

private fun Timestamp?.toKotlinLocalDateOrDefault(default: LocalDate = LocalDate(1970, 1, 1), systemZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    this?.toKotlinLocalDate(systemZone) ?: default

private fun Instant.toTimestamp(): Timestamp = Timestamp(Date(this.toEpochMilliseconds()))
private fun Instant?.toTimestampOrNull(): Timestamp? = this?.toTimestamp()

private fun LocalDate.toTimestamp(): Timestamp {
    val javaInstant = java.time.LocalDate.parse(this.toString()).atStartOfDay(ZoneId.systemDefault()).toInstant()
    return Timestamp(Date(javaInstant.toEpochMilli()))
}

private fun LocalDate?.toTimestampOrNull(): Timestamp? = this?.toTimestamp()

// -------------------- MAPPERS: Firestore DTO -> Domain --------------------

fun MedicationEventFirestoreDto.toDomain(): MedicationEvent {
    return MedicationEvent(
        id = this.id,
        petId = this.petId,
        title = this.title,
        medicationId = this.medicationId,
        takenAt = this.takenAt?.toKotlinInstantOrNull() ?: Instant.DISTANT_PAST,
        status = this.status,
        notes = this.notes,
        scheduledAt = this.scheduledAt?.toKotlinInstantOrNull() ?: Instant.DISTANT_PAST,
    )
}

fun MedicationFirestoreDto.toDomain(): Medication {
    return Medication(
        id = this.id,
        petId = this.petId,
        name = this.name,
        form = this.form,
        dose = this.dose,
        notes = this.notes,
        active = this.active,
        createdAt = this.createdAt?.toKotlinLocalDateOrDefault() ?: LocalDate(1970, 1, 1),
        from = this.from?.toKotlinLocalDateOrDefault() ?: LocalDate(1970, 1, 1),
        to = this.to?.toKotlinLocalDateOrDefault(),
        reccurenceString = this.reccurenceString,
        // Note: MedicationFirestoreDto.times currently stores List<String> in the project; reuse DateConverter.stringToInstant
        times = this.times.map { LocalTime.parse(it)}
    )
}

fun WalkTrackPointFirestoreDto.toDomain(): WalkTrackPoint {
    return WalkTrackPoint(
        id = this.id,
        walkId = this.walkId,
        ts = this.ts.toKotlinInstant(),
        lat = this.lat,
        lon = this.lon
    )
}

fun WalkFirestoreDto.toDomain(): Walk {
    return Walk(
        id = this.id,
        petId = this.petId,
        startedAt = this.startedAt.toKotlinLocalDate(),
        endedAt = this.endedAt?.toKotlinLocalDateOrDefault(),
        durationSec = this.durationSec,
        distanceMeters = this.distanceMeters,
        steps = this.steps,
        pending = this.pending,
        createdAt = this.createdAt.toKotlinLocalDate()
    )
}

fun UserFirestoreDto.toDomain(): User = User(
    email = this.email,
    displayName = this.displayName,
    id = this.id
)

fun TaskFirestoreDto.toDomain(docId: String): Task {
    return Task(
        id = docId,
        seriesId = seriesId,
        petId = requireNotNull(petId),
        type = type,
        title = title,
        notes = notes,
        status = status,
        priority = priority,
        createdAt = createdAt?.toKotlinLocalDate() ?: LocalDate(1970, 1, 1),
        date = date?.toKotlinInstant() ?: Instant.DISTANT_PAST,
        rrule = rrule
    )
}

fun PetShareCodeFirestoreDto.toDomain(): PetShareCode = PetShareCode(
    id = this.id,
    petId = this.petId,
    code = this.code,
    createdAt = this.createdAt?.toKotlinLocalDateOrDefault() ?: LocalDate(1970, 1, 1),
    expiresAt = this.expiresAt.toKotlinInstant()
)

fun PetMemberFirestoreDto.toDomain(): PetMember = PetMember(
    id = this.id,
    petId = this.petId,
    userId = this.userId,
    createdAt = this.createdAt.toKotlinLocalDate()
)

fun PetFirestoreDto.toDomain(): Pet = Pet(
    id = this.id,
    ownerUserId = this.ownerUserId,
    name = this.name,
    species = this.species,
    breed = this.breed,
    sex = this.sex,
    birthDate = this.birthDate?.toKotlinLocalDateOrDefault() ?: LocalDate(1970, 1, 1),
    avatarThumbUrl = this.avatarThumbUrl,
    createdAt = this.createdAt?.toKotlinLocalDateOrDefault() ?: LocalDate(1970, 1, 1)
)

fun NotificationSettingFirestoreDto.toDomain(): NotificationSettings = NotificationSettings(
    id = this.id,
    userId = this.userId,
    category = this.category,
    updatedAt = this.updatedAt.toKotlinInstant(),
    enabled = this.enabled
)


// -------------------- MAPPERS: Domain -> Firestore DTO --------------------

fun MedicationEvent.toFirestoreDto(): MedicationEventFirestoreDto = MedicationEventFirestoreDto(
    id = this.id,
    petId = this.petId,
    title = this.title,
    medicationId = this.medicationId,
    takenAt = this.takenAt.toTimestamp(),
    status = this.status,
    notes = this.notes,
    scheduledAt = this.scheduledAt.toTimestamp()
)

fun Medication.toFirestoreDto(): MedicationFirestoreDto = MedicationFirestoreDto(
    id = this.id,
    petId = this.petId,
    name = this.name,
    form = this.form,
    dose = this.dose,
    notes = this.notes,
    active = this.active,
    createdAt = this.createdAt.toTimestamp(),
    from = this.from.toTimestamp(),
    to = this.to?.toTimestamp()!!,
    reccurenceString =  this.reccurenceString,
    times = this.times.map { it.toString() }
)

fun WalkTrackPoint.toFirestoreDto(): WalkTrackPointFirestoreDto = WalkTrackPointFirestoreDto(
    id = this.id,
    walkId = this.walkId,
    ts = this.ts.toTimestamp(),
    lat = this.lat,
    lon = this.lon
)

fun Walk.toFirestoreDto(): WalkFirestoreDto = WalkFirestoreDto(
    id = this.id,
    petId = this.petId,
    startedAt = this.startedAt.toTimestamp(),
    endedAt = this.endedAt?.toTimestampOrNull(),
    durationSec = this.durationSec,
    distanceMeters = this.distanceMeters,
    steps = this.steps,
    pending = this.pending,
    createdAt = this.createdAt.toTimestamp()
)

fun User.toFirestoreDto(): UserFirestoreDto = UserFirestoreDto(
    email = this.email,
    displayName = this.displayName,
    id = this.id
)

fun Task.toFirestoreDto(rruleOverride: String?): TaskFirestoreDto =
    TaskFirestoreDto(
        id = this.id,
        seriesId = this.seriesId,
        petId = this.petId,
        type = this.type,
        title = this.title,
        description = null,
        notes = this.notes,
        status = this.status,
        priority = this.priority,
        createdAt = this.createdAt.toTimestamp(),
        date = this.date.toTimestamp(),
        rrule = rruleOverride ?: this.rrule,
        isRecurring = (rruleOverride ?: this.rrule) != null
    )

fun PetShareCode.toFirestoreDto(): PetShareCodeFirestoreDto = PetShareCodeFirestoreDto(
    id = this.id,
    petId = this.petId,
    code = this.code,
    expiresAt = this.expiresAt.toTimestamp(),
    createdAt = this.createdAt.toTimestamp()
)

fun PetMember.toFirestoreDto(): PetMemberFirestoreDto = PetMemberFirestoreDto(
    id = this.id,
    petId = this.petId,
    userId = this.userId,
    createdAt = this.createdAt.toTimestamp()
)

fun Pet.toFirestoreDto(): PetFirestoreDto = PetFirestoreDto(
    id = this.id,
    ownerUserId = this.ownerUserId,
    name = this.name,
    species = this.species,
    breed = this.breed,
    sex = this.sex,
    birthDate = this.birthDate.toTimestamp(),
    avatarThumbUrl = this.avatarThumbUrl,
    createdAt = this.createdAt.toTimestamp()
)

fun NotificationSettings.toFirestoreDto(): NotificationSettingFirestoreDto = NotificationSettingFirestoreDto(
    id = this.id,
    userId = this.userId,
    category = this.category,
    updatedAt = this.updatedAt.toTimestamp(),
    enabled = this.enabled
)



