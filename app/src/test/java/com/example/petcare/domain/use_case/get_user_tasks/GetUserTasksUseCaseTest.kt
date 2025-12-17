package com.example.petcare.domain.use_case.get_user_tasks

import com.example.petcare.common.Resource
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakeTaskRepository
import com.example.petcare.domain.model.PetMember
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
import java.util.UUID

class GetUserTasksUseCaseTest {

    private lateinit var getUserTasksUseCase: GetUserTasksUseCase
    private lateinit var userProvider: FakeUserProvider
    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var petMemberRepository: FakePetMemberRepository

    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        taskRepository = FakeTaskRepository()
        petMemberRepository = FakePetMemberRepository()

        getUserTasksUseCase = GetUserTasksUseCase(
            taskRepository,
            userProvider,
            petMemberRepository
        )
    }

    @Test
    fun `invoke should return Success with tasks when user is logged in and has pets with tasks`() = runTest {
        // Given
        val userId = "user123"
        val petId1 = "pet1"
        val petId2 = "pet2"
        userProvider.setUserId(userId)

        // Add user as member of pets
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        petMemberRepository.addPetMember(PetMember(UUID.randomUUID().toString(), petId1, userId, today))
        petMemberRepository.addPetMember(PetMember(UUID.randomUUID().toString(), petId2, userId, today))

        // Create tasks for the pets
        val task1 = Task(
            id = "task1",
            seriesId = null,
            petId = petId1,
            type = taskTypeEnum.walk,
            title = "Walk Pet 1",
            notes = null,
            status = taskStatusEnum.planned,
            priority = taskPriorityEnum.normal,
            createdAt = today,
            date = Clock.System.now()
        )
        val task2 = Task(
            id = "task2",
            seriesId = null,
            petId = petId2,
            type = taskTypeEnum.feeding,
            title = "Feed Pet 2",
            notes = null,
            status = taskStatusEnum.done,
            priority = taskPriorityEnum.high,
            createdAt = today,
            date = Clock.System.now()
        )
        taskRepository.createTask(task1, null)
        taskRepository.createTask(task2, null)

        // When
        val result = getUserTasksUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val tasks = (result.last() as Resource.Success).data
        assertEquals(2, tasks?.size)
        assertTrue(tasks?.any { it.id == "task1" } == true)
        assertTrue(tasks?.any { it.id == "task2" } == true)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()

        // When
        val result = getUserTasksUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User not logged in", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Success with empty list when user has no pets`() = runTest {
        // Given
        val userId = "user123"
        userProvider.setUserId(userId)
        // No pets added to member repository for this user

        // When
        val result = getUserTasksUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val tasks = (result.last() as Resource.Success).data
        assertTrue(tasks?.isEmpty() == true)
    }
}

