package com.example.petcare.domain.use_case.get_task_by_id

import com.example.petcare.common.Resource
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakeTaskRepository
import com.example.petcare.domain.model.Task
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTaskByIdUseCaseTest {

    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var userProvider: FakeUserProvider
    private lateinit var petProvider: FakePetProvider
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var petMemberRepository: FakePetMemberRepository

    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        petProvider = FakePetProvider()
        taskRepository = FakeTaskRepository()
        petMemberRepository = FakePetMemberRepository()

        getTaskByIdUseCase = GetTaskByIdUseCase(
            userProvider,
            petProvider,
            taskRepository,
            petMemberRepository
        )
    }

    @Test
    fun `invoke should return Success when user is logged in and is member of the pet`() = runTest {
        // Given
        val userId = "user123"
        val petId = "pet123"
        val taskId = "task123"
        userProvider.setUserId(userId)

        // Create a task for the pet
        val task = Task(
            id = taskId,
            seriesId = null,
            petId = petId,
            type = taskTypeEnum.walk,
            title = "Walk",
            notes = null,
            status = taskStatusEnum.planned,
            priority = taskPriorityEnum.normal,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            date = Clock.System.now()
        )
        taskRepository.createTask(task, null)

        // When
        val result = getTaskByIdUseCase(taskId).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val retrievedTask = (result.last() as Resource.Success).data
        assertEquals(taskId, retrievedTask?.id)
        assertEquals("Walk", retrievedTask?.title)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()
        val taskId = "task123"

        // When
        val result = getTaskByIdUseCase(taskId).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User not logged in", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when task not found`() = runTest {
        // Given
        val userId = "user123"
        userProvider.setUserId(userId)
        val taskId = "nonExistentTask"

        // When
        val result = getTaskByIdUseCase(taskId).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("Task not found", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when user is not member of the pet`() = runTest {
        // Given
        val userId = "user123"
        val petId = "otherPet"
        val taskId = "task123"
        userProvider.setUserId(userId)

        // Create a task for a pet the user is NOT a member of
        // FakePetMemberRepository has hardcoded user123, pet123. So otherPet will fail check.
        val task = Task(
            id = taskId,
            seriesId = null,
            petId = petId,
            type = taskTypeEnum.walk,
            title = "Walk",
            notes = null,
            status = taskStatusEnum.planned,
            priority = taskPriorityEnum.normal,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            date = Clock.System.now()
        )
        taskRepository.createTask(task, null)

        // When
        val result = getTaskByIdUseCase(taskId).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User does not have permission to view this task", (result.last() as Resource.Error).message)
    }
}

