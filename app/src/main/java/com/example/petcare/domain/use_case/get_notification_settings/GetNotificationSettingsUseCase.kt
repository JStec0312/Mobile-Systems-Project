package com.example.petcare.domain.use_case.get_notification_settings
import com.example.petcare.common.Resource
import com.example.petcare.domain.model.NotificationSettings
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.INotificationSettingsRepository
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
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
        emit(Resource.Error("Not implemented"))
        return@flow
    }
}