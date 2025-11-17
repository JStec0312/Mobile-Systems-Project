package com.example.petcare.exceptions

sealed class Failure(override val message: String) : Exception(message) {
    object NetworkError: Failure("Network Error")
    object ServerError: Failure("Server Error")
    data class UnknownError(val exception: Throwable): Failure("Unknown error: ${exception.message}")
}