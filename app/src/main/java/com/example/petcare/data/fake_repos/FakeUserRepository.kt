package com.example.petcare.data.fake_repos
import java.util.UUID
import com.example.petcare.data.dto.UserDto
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
class FakeUserRepository : IUserRepository {
    private val users = mutableMapOf<String, Pair<UserDto, String>>()

    override suspend fun createUser(
        email: String,
        password: String,
        displayName: String
    ): UserDto {

        // --- Symulacja błędów ---
        // Używamy "magicznych" stringów, aby frontend mógł testować scenariusze błędów
        when {
            email.contains("existing@user.com") -> {
                throw AuthFailure.EmailAlreadyInUse()
            }
            email.contains("network@error.com") -> {
                throw Failure.NetworkError()
            }
            email.contains("server@error.com") -> {
                throw Failure.ServerError()
            }
            email.contains("unknown@error.com") -> {
                throw Failure.UnknownError()
            }
        }
        if (users.containsKey(email)) {
            throw AuthFailure.EmailAlreadyInUse()
        }

        val user = UserDto(
            email = email,
            displayName = displayName,
            id = UUID.randomUUID().toString()
        )
        users[email] = Pair(user, password)
        return user
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): UserDto {
        val user = users[email]
        if (user==null ){
            throw AuthFailure.UserNotFound()
        }
        if (user.second != password) {
            throw AuthFailure.InvalidCredentials()
        }
        when{
            email.contains("network@error.com") -> {
                throw Failure.NetworkError()
            }
            email.contains("server@error.com") -> {
                throw Failure.ServerError()
            }
            email.contains("unknown@error.com") -> {
                throw Failure.UnknownError()
            }
        }
        return user.first
        }
}