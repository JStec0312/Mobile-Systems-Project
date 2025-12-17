package com.example.petcare.domain.use_case.get_tasks

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
import kotlin.time.Duration.Companion.days

class GetTasksUseCaseTest {

    private lateinit var getTasksUseCase: GetTasksUseCase
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

        getTasksUseCase = GetTasksUseCase(
            userProvider,
            petProvider,
            taskRepository,
            petMemberRepository
        )
    }

    @Test
    fun `invoke should return Success with tasks when user is logged in, pet selected and is member`() = runTest {
        // Given
        val userId = "user123"
        val petId = "pet123"
        userProvider.setUserId(userId)
        petProvider.setCurrentPetId(petId)

        // Create tasks for the pet
        val task1 = Task(
            id = "task1",
            seriesId = null,
            petId = petId,
            type = taskTypeEnum.walk,
            title = "Walk",
            notes = null,
            status = taskStatusEnum.planned,
            priority = taskPriorityEnum.normal,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            date = Clock.System.now() + 1.days
        )
        val task2 = Task(
            id = "task2",
            seriesId = null,
            petId = petId,
            type = taskTypeEnum.feeding,
            title = "Feed",
            notes = null,
            status = taskStatusEnum.done,
            priority = taskPriorityEnum.high,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            date = Clock.System.now() + 2.days
        )
        taskRepository.createTask(task1, null)
        taskRepository.createTask(task2, null)

        // When
        val result = getTasksUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val tasks = (result.last() as Resource.Success).data
        assertEquals(2, tasks?.size)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()
        petProvider.setCurrentPetId("pet123")

        // When
        val result = getTasksUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User not logged in", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when no pet selected`() = runTest {
        // Given
        userProvider.setUserId("user123")
        petProvider.setCurrentPetId(null)

        // When
        val result = getTasksUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("No pet selected", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when user is not member of the pet`() = runTest {
        // Given
        val userId = "user123"
        val petId = "otherPet"
        userProvider.setUserId(userId)
        petProvider.setCurrentPetId(petId)

        // FakePetMemberRepository has hardcoded user123, pet123. So otherPet will fail check.

        // When
        val result = getTasksUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User does not have permission to view tasks for this pet", (result.last() as Resource.Error).message)
    }
}

