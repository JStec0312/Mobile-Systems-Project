package com.example.petcare.domain.use_case.sign_in
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.User
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IUserRepository
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInUseCase  @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val userRepository: IUserRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        delayMs: Long = 600,
        shouldFail: Boolean = false,
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading<User>())
        delay(delayMs)
        if (shouldFail) {
            emit(Resource.Error<User>("Failed to sign in"))

        } else {
            val user = User(
                id = UUID.randomUUID(),
                email = email,
                display_name = "John Doe",
            )

            // Simulate saving user session
            userProvider.setUserId(user.id)
            emit(Resource.Success<User>(user))
        }
    }
}