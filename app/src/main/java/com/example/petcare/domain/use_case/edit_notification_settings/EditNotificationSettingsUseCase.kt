package com.example.petcare.domain.use_case.edit_notification_settings
import android.app.NotificationChannel
import com.example.petcare.common.Resource
import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.domain.model.NotificationSettings
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.INotificationRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EditNotificationSettingsUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val notificationSettingsRepository: INotificationRepository
){
    operator fun invoke(
        category: notificationCategoryEnum,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        try{
            val userId = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error<Unit>("User not logged in"))
                return@flow
            }
            notificationSettingsRepository.toggleNotificationSettingsForUser(
                userId = userId,
                newCategory = category
            );

            emit(Resource.Success<Unit>(Unit));
        } catch (e: Failure){
            emit(Resource.Error<Unit>(e.message ?: "An unexpected error occurred"))
        } catch (e: GeneralFailure){
            emit(Resource.Error<Unit>(e.message ?: "An unexpected error occurred"))
        }
    }
}