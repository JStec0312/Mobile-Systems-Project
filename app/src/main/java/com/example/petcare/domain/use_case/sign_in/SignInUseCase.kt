package com.example.petcare.domain.use_case.sign_in
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.User
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IUserRepository
import com.example.petcare.exceptions.AuthFailure
import com.example.petcare.exceptions.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class SignInUseCase  @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val userRepository: IUserRepository
) {
    operator fun invoke(
        email: String,
        password: String,
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading<User>())
        if (email.isBlank() || password.isBlank()) {
            emit(Resource.Error<User>("Email and password cannot be empty"))
            return@flow
        }
        try{
            val user: User = userRepository.signInWithEmailAndPassword(email, password)
            //userProvider.setUserId(user.id)
            emit(Resource.Success<User>(user))
            return@flow
        }  catch(e: AuthFailure.InvalidCredentials){
            emit(Resource.Error<User>(e.message))
            return@flow
        } catch (e: AuthFailure.UserNotFound){
            emit(Resource.Error<User>(e.message))
            return@flow
        } catch (e: Failure.NetworkError){
            emit(Resource.Error<User>(e.message))
            return@flow
        } catch(e: Failure.ServerError){
            emit(Resource.Error<User>(e.message))
            return@flow
        } catch(e: Failure.UnknownError){
            emit(Resource.Error<User>(e.message))
            return@flow
        } catch (e: Exception){
            emit(Resource.Error<User>("An unexpected error occurred: ${e.message}"))
            Timber.d("Login unexpected error: ${e.message}")
            return@flow
        }

    }
}