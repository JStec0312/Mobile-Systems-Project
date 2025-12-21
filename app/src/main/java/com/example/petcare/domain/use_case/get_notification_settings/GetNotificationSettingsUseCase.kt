package com.example.petcare.domain.use_case.get_notification_settings
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.NotificationSettings
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.INotificationRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class GetNotificationSettingsUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val notificationSettingsRepository: INotificationRepository
){
    operator fun invoke(
        petId: UUID,
        delayMs: Long = 600, //@NOTE Simulated delay
        shouldFail : Boolean = false, //@NOTE Simulated failure
    ): Flow<Resource<List<NotificationSettings>>> = flow {
        emit(Resource.Loading<List<NotificationSettings>>())
        try{
            val userId = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error<List<NotificationSettings>>("User not logged in"))
                return@flow
            }
            val settings = notificationSettingsRepository.getForUser(
                userId = userId
            );
            emit(Resource.Success<List<NotificationSettings>>(settings));
        } catch (e: Failure){
            emit(Resource.Error<List<NotificationSettings>>(e.message ?: "An unexpected error occurred"))
        } catch (e: GeneralFailure){
            emit(Resource.Error<List<NotificationSettings>>(e.message ?: "An unexpected error occurred"))
        }
    }
}