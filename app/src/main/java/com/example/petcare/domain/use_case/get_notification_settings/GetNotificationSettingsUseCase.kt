package com.example.petcare.domain.use_case.get_notification_settings
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.NotificationSettings
import com.example.petcare.common.notificationChannelEnum
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.INotificationSettingsRepository
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import javax.inject.Inject


class GetNotificationSettingsUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val notificationSettingsRepository: INotificationSettingsRepository
){
    operator fun invoke(
        petId: UUID,
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail : Boolean = false, //@NOTE Simulated failure
    ): Flow<Resource<List<NotificationSettings>>> = flow {
        emit(Resource.Loading<List<NotificationSettings>>())
        delay(delayMs)
        if (shouldFail){
            emit(Resource.Error("Failed to get notification settings"))

        } else{
            val example_settings = ArrayList<NotificationSettings>()
            val setting1 = NotificationSettings(
                id = UUID.randomUUID(),
                pet_id = petProvider.getCurrentPetId(),
                user_id = userProvider.getUserId(),
                channel = notificationChannelEnum.meds,
                created_at = Clock.System.now(),
            )
            val setting2 = NotificationSettings(
                id = UUID.randomUUID(),
                pet_id = petProvider.getCurrentPetId(),
                user_id = userProvider.getUserId(),
                channel = notificationChannelEnum.tasks,
                created_at = Clock.System.now(),
            )
            example_settings.add(setting1)
            example_settings.add(setting2)

            emit(Resource.Success<List<NotificationSettings>>(example_settings))
        }
    }
}