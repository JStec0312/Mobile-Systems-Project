package com.example.petcare.domain.use_case.add_task

import com.example.petcare.common.Resource
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakeTaskRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddTaskUseCaseTest {

    private lateinit var addTaskUseCase: AddTaskUseCase
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
        addTaskUseCase = AddTaskUseCase(
            taskRepository,
            userProvider,
            petProvider,
            petMemberRepository
        )
    }
    @Test
    fun `invoke should return Success when user and pet are set and repository succeeds`() = runTest {
        // Given
        userProvider.setUserId("user123")
        petProvider.setCurrentPetId("pet123")
        val now = Clock.System.now()

        // When
        val result = addTaskUseCase(
            type = taskTypeEnum.training,
            title = "Grooming",
            notes = "Brush fur",
            priority = taskPriorityEnum.high,
            date = now,
            rrule = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()
        petProvider.setCurrentPetId("pet123")
        val now = Clock.System.now()

        // When
        val result = addTaskUseCase(
            type = taskTypeEnum.training,
            title = "Grooming",
            notes = "Brush fur",
            priority = taskPriorityEnum.high,
            date = now,
            rrule = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User not logged in", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when no pet is selected`() = runTest {
        // Given
        userProvider.setUserId("user123")
        petProvider.setCurrentPetId(null)
        val now = Clock.System.now()

        // When
        val result = addTaskUseCase(
            type = taskTypeEnum.training,
            title = "Grooming",
            notes = "Brush fur",
            priority = taskPriorityEnum.high,
            date = now,
            rrule = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("No pet selected", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when user is not a member of the pet`() = runTest {
        // Given
        userProvider.setUserId("user1237")
        petProvider.setCurrentPetId("pet123")

        val now = Clock.System.now()

        // When
        val result = addTaskUseCase(
            type = taskTypeEnum.training,
            title = "Grooming",
            notes = "Brush fur",
            priority = taskPriorityEnum.high,
            date = now,
            rrule = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User does not have access to this pet", (result.last() as Resource.Error).message)
    }
}

