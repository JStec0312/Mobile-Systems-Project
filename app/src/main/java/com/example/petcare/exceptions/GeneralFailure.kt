package com.example.petcare.exceptions;

sealed class GeneralFailure(override val message: String) : Failure(message) {
    class PetNotFound(msg: String? = "Pet not found") : GeneralFailure(msg!!)
}
