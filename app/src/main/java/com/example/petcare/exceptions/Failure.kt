package com.example.petcare.exceptions

sealed class Failure(override val message: String?) : Exception(message) {
    class NetworkError(msg: String? = "Network error"): Failure(msg)
    class ServerError(msg : String? = "Server error"): Failure("$msg")
    class  UnknownError(msg: String? = "Unknown error"): Failure(msg)
}