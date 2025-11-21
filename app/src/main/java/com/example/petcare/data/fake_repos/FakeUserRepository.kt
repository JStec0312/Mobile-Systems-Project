package com.example.petcare.data.fake_repos
import com.example.petcare.config.DeveloperSettings
import com.example.petcare.data.dto.PetDto
import java.util.UUID
import com.example.petcare.data.dto.UserDto
import com.example.petcare.domain.model.User
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

class FakeUserRepository @Inject constructor() : IUserRepository {
        private val users =  mutableListOf<UserDto>();
        private val passwords = mutableMapOf<String, String>(); // email -> password

        init {
            val testUser = UserDto(
                id = DeveloperSettings.TEST_USER_ID,
                email = DeveloperSettings.TEST_USER_EMAIL,
                displayName = "Test User",
            );
            users.add(testUser);
            passwords[testUser.email] = DeveloperSettings.TEST_USER_PASSWORD;
        }

    override suspend fun createUser(user: User, password: String): UserDto {
        // --- Symulacja błędów ---
        // Używamy "magicznych" stringów, aby frontend mógł testować scenariusze błędów
        when {
            user.email.contains("existing@user.com") -> {
                throw AuthFailure.EmailAlreadyInUse()
            }
            user.email.contains("network@error.com") -> {
                throw Failure.NetworkError()
            }
            user.email.contains("server@error.com") -> {
                throw Failure.ServerError()
            }
            user.email.contains("unknown@error.com") -> {
                throw Failure.UnknownError()
            }
        }
        val userDto: UserDto = user.toDto();
        users.add(userDto);
        passwords[user.email] = password;
        return userDto;
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): UserDto {
        Timber.d("signInWithEmailAndPassword: $email, $password")
        Timber.d("Stored users: $users")
        val user: UserDto = users.find { it.email == email }
            ?: throw AuthFailure.UserNotFound()
        val storedPassword: String? = passwords[email]
        if (storedPassword == null || storedPassword != password) {
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
        return user;
        }

    override suspend fun getUserById(userId: String) {
        TODO("Not yet implemented")
    }
}