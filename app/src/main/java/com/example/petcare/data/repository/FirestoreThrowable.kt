package com.example.petcare.data.repository

import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.net.UnknownHostException
import java.io.IOException

/**
 * Centralne mapowanie wyjątków z Firestore na domenowe Failure.
 *
 * Użycie:
 *  try { ... } catch (t: Throwable) { throw FirestoreThrowable.map(t, "opName") }
 */
object FirestoreThrowable {

    fun map(t: Throwable, op: String): Throwable {
        if (t is CancellationException) return t
        if (t is GeneralFailure) return t
        val root = unwrap(t)
        if (root is FirebaseFirestoreException) {
            return when (root.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    Failure.ServerError("[$op] PERMISSION_DENIED: ${root.message}")

                FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                    Failure.ServerError("[$op] UNAUTHENTICATED: ${root.message}")

                FirebaseFirestoreException.Code.UNAVAILABLE,
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                    Failure.NetworkError("[$op] Network error: ${root.message}")

                FirebaseFirestoreException.Code.ABORTED,
                FirebaseFirestoreException.Code.INTERNAL,
                FirebaseFirestoreException.Code.DATA_LOSS ->
                    Failure.ServerError("[$op] Server error (${root.code}): ${root.message}")

                else ->
                    Failure.UnknownError("[$op] Firestore error (${root.code}): ${root.message}")
            }
        }
        if (root is UnknownHostException || root is IOException) {
            return Failure.NetworkError("[$op] Network error: ${root.message}")
        }
        return Failure.UnknownError("[$op] ${root.message ?: root::class.java.simpleName}")
    }

    private fun unwrap(t: Throwable): Throwable {
        if (t is ExecutionException && t.cause != null) return unwrap(t.cause!!)
        if (t.cause != null && t::class == RuntimeException::class) return unwrap(t.cause!!)
        return t
    }
}
