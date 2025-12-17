package com.example.petcare.data.repository

object FirestorePaths {
    // top-level collections
    const val USERS = "users"
    const val PETS = "pets"
    const val MEDICATIONS = "medications"
    const val MEDICATION_EVENTS = "medicationEvents"
    const val PET_SHARE_CODES = "petShareCodes"
    const val WALKS = "walks"
    const val PET_MEMBERS = "petMembers" // jesli globalnie

    // user subcollections
    const val USER_NOTIFICATION_SETTINGS = "notificationSettings"
    const val USER_PET_MEMBERS = "petMembers" // jesli per-user

    // walk subcollections
    const val WALK_TRACK_POINTS = "trackPoints"

    // document builders
    fun userDoc(userId: String) = "$USERS/$userId"
    fun petDoc(petId: String) = "$PETS/$petId"
    fun medicationDoc(medId: String) = "$MEDICATIONS/$medId"
    fun medicationEventDoc(eventId: String) = "$MEDICATION_EVENTS/$eventId"

    fun userNotificationSettingDoc(userId: String, category: String) =
        "${userDoc(userId)}/$USER_NOTIFICATION_SETTINGS/$category"

    fun userPetMemberDoc(userId: String, petId: String) =
        "${userDoc(userId)}/$USER_PET_MEMBERS/$petId"

    fun walkDoc(walkId: String) = "$WALKS/$walkId"
    fun walkTrackPointDoc(walkId: String, pointId: String) =
        "${walkDoc(walkId)}/$WALK_TRACK_POINTS/$pointId"
}
