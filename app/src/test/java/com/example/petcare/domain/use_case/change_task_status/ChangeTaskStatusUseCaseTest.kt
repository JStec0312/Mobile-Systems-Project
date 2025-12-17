package com.example.petcare.domain.use_case.change_task_status

import com.example.petcare.common.Resource
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakeTaskRepository
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.ITaskRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ChangeTaskStatusUseCaseTest {

    private lateinit var changeTaskStatusUseCase: ChangeTaskStatusUseCase
    private lateinit var taskRepository: ITaskRepository
    private lateinit var userProvider: IUserProvider
    private lateinit var petProvider: IPetProvider
    private lateinit var petMemberRepository: IPetMemberRepository

    @Before
    fun setUp() {
        taskRepository = FakeTaskRepository()
        userProvider = FakeUserProvider()
        petProvider = FakePetProvider()
        petMemberRepository = FakePetMemberRepository()
        changeTaskStatusUseCase = ChangeTaskStatusUseCase(
            taskRepository,
            userProvider,
            petProvider,
            petMemberRepository
        )
    }

    @Test
    fun `invoke should return Success when user is logged in and is a member of the pet`() = runTest {
        // Given
        val userId = "user123"
        val petId = "pet123"
        val taskId = UUID.randomUUID().toString()
        val task = Task(
            id = taskId,
            seriesId = null,
            petId = petId,
            type = taskTypeEnum.feeding,
            title = "Feed Pet",
            notes = null,
            status = taskStatusEnum.planned,
            priority = taskPriorityEnum.normal,
            createdAt = com.example.petcare.common.utils.DateConverter.localDateNow(),
            date = Clock.System.now()
        )

        (userProvider as FakeUserProvider).setUserId(userId)
        taskRepository.createTask(task, null)
        // Note: FakePetMemberRepository already has a member with userId="user123" and petId="pet123" in its init block

        // When
        val result = changeTaskStatusUseCase(taskId, taskStatusEnum.done).toList()

        // Then
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Success)

        val updatedTask = taskRepository.getTaskById(taskId)
        assertEquals(taskStatusEnum.done, updatedTask.status)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        val taskId = UUID.randomUUID().toString()
        (userProvider as FakeUserProvider).clearUserData()

        // When
        val result = changeTaskStatusUseCase(taskId, taskStatusEnum.done).toList()

        // Then
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
        assertEquals("User not logged in", (result[1] as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when user is not a member of the pet`() = runTest {
        // Given
        val userId = "otherUser"
        val petId = "pet123"
        val taskId = UUID.randomUUID().toString()
        val task = Task(
            id = taskId,
            seriesId = null,
            petId = petId,
            type = taskTypeEnum.feeding,
            title = "Feed Pet",
            notes = null,
            status = taskStatusEnum.planned,
            priority = taskPriorityEnum.normal,
            createdAt = com.example.petcare.common.utils.DateConverter.localDateNow(),
            date = Clock.System.now()
        )

        (userProvider as FakeUserProvider).setUserId(userId)
        taskRepository.createTask(task, null)

        // When
        val result = changeTaskStatusUseCase(taskId, taskStatusEnum.done).toList()

        // Then
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
        assertEquals("User does not have permission to change task status for this pet", (result[1] as Resource.Error).message)
    }
}
