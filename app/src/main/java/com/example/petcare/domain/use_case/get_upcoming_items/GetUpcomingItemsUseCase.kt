package com.example.petcare.domain.use_case.get_upcoming_items

import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.domain.model.UpcomingItem
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.ITaskRepository
import kotlinx.datetime.Instant
import timber.log.Timber
import javax.inject.Inject

class GetUpcomingItemsUseCase @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val medicationRepository: IMedicationRepository,
    private val memberRepository: IPetMemberRepository,
    private val userProvider: IUserProvider,
    private val medicationEventRepository: IMedicationEventRepository
) {
    operator suspend fun invoke(
        from: Instant,
        to: Instant
    ): List<UpcomingItem>{
        val userId = userProvider.getUserId() ?: return emptyList()
        val petIds = memberRepository.getPetIdsByUserId(userId)
        val tasks = taskRepository.getTasksByPetIdsInDateRange(petIds, from, to);
        val taskItems = tasks.map {
            UpcomingItem(
                id = it.id,
                petId = it.petId,
                type = notificationCategoryEnum.tasks,
                title = it.title,
                at = it.date
            )
        }
        Timber.d("Tasks found in date range: ${taskItems.map { it.title }}")
        val meds = medicationEventRepository.getUpcomingMedicationEventsForUserInDateRange(petIds, from, to);
        val medItems = meds.map{
            UpcomingItem(
                id = it.id,
                petId = it.petId,
                type = notificationCategoryEnum.meds,
                title = it.title,
                at = it.scheduledAt
            )
        }
        return medItems + taskItems
    }
}