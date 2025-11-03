package com.example.petcare.domain.use_case.edit_notification_settings
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.NotificationSettings
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.INotificationSettingsRepository
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import javax.inject.Inject

class EditNotificationSettingsUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val notificationSettingsRepository: INotificationSettingsRepository
){
    operator fun invoke(
        settings: NotificationSettings,
        delayMs: Long = 600,
        shouldFail : Boolean = false,
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error<Unit>("Failed to edit notification settings"))

        } else{
            emit(Resource.Success<Unit>(Unit))
        }
    }
}