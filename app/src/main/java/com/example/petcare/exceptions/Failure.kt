package com.example.petcare.exceptions

sealed class Failure(override val message: String) : Exception(message) {
    class NetworkError: Failure("Network Error")
    class ServerError: Failure("Server Error")
    class  UnknownError(): Failure("Unknown error")
}