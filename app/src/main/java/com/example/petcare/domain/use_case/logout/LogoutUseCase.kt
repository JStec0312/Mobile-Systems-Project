package com.example.petcare.domain.use_case.logout

import com.example.petcare.common.Resource
import com.example.petcare.domain.providers.IUserProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userProvider: IUserProvider
) {
    operator fun invoke(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val userId = userProvider.getUserId()
        if (userId != null) {
            userProvider.clearUserData()
            emit(Resource.Success(Unit))
        } else {
            emit(Resource.Error("User not logged in"))
        }
    }
}