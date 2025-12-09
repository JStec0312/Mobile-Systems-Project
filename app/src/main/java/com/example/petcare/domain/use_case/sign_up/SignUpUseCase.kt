package com.example.petcare.domain.use_case.sign_up

import com.example.petcare.common.Resource
import com.example.petcare.common.utils.EmailValidator
import com.example.petcare.config.Settings
import com.example.petcare.domain.model.User
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.repository.INotificationRepository
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val petProvider: IPetProvider,
    private val userRepository: IUserRepository,
    private val notificationRepository: INotificationRepository
) {

    operator fun invoke(
        name: String,
        email: String,

        password: String,
        confirmPassword: String,

    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        if (name.isBlank()) {
            emit(Resource.Error<Unit>("Name cannot be blank"))
            return@flow
        };
        if (email.isBlank() || !EmailValidator.isValidEmail(email)) {
            emit(Resource.Error<Unit>("Email cannot be blank"))
            return@flow
        };
        if (password.isBlank()) {
            emit(Resource.Error<Unit>("Password cannot be blank"))
            return@flow

        };
        if (password != confirmPassword) {
            emit(Resource.Error<Unit>("Passwords do not match"))
            return@flow
        }
        if (password.length < Settings.MIN_PASSWORD_LENGTH){
            emit(Resource.Error<Unit>("Password must be at least ${Settings.MIN_PASSWORD_LENGTH} characters"))
            return@flow
        }
        try {
           val user: User = User(
               id = UUID.randomUUID().toString(),
               email = email,
               displayName = name,
           )
            userRepository.createUser(user, password)
            notificationRepository.createNotificationChannelForNewUser(user.id)
        } catch(e: AuthFailure.EmailAlreadyInUse ){
            emit(Resource.Error<Unit>(e.message))
            return@flow
        } catch(e: Failure) {
            emit(Resource.Error<Unit>(e.message))
            return@flow
        }
        emit(Resource.Success(Unit))
        return@flow
    }
}