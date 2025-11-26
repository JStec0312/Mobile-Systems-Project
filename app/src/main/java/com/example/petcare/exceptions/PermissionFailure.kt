package com.example.petcare.exceptions

sealed class PermissionFailure(override val message: String?) : Failure(message) {
    class LocationPermissionDenied(msg: String? = "Location permission denied") : PermissionFailure(message = msg)
}