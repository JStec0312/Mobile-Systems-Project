package com.example.petcare.exceptions;

sealed class GeneralFailure(override val message: String) : Failure(message) {
    class PetNotFound(msg: String? = "Pet not found") : GeneralFailure(msg!!)

    class MedicationNotFound(msg: String? = "Medication not found") : GeneralFailure(msg!!)

    class TaskNotFound(msg: String? = "Task not found") : GeneralFailure(msg!!)
    class WalkNotFound(msg: String? = "Walk not found") : GeneralFailure(msg!!)

    class WalkAlreadyEnded(msg: String? = "Walk has already been ended") : GeneralFailure(msg!!)
    class InvalidIntervalPassed(msg: String? = "Invalid interval passed") : GeneralFailure(msg!!)

    class NotificationSettingNotFound(msg: String? = "Notification setting not found") : GeneralFailure(msg!!)
    class DataCorruption(msg: String? = "Data corruption detected") : GeneralFailure(msg!!)


}
