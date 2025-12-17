package com.example.petcare.domain.use_case.add_medication

import com.example.petcare.common.Resource
import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakeMedicationEventRepository
import com.example.petcare.data.fake_repos.FakeMedicationRepository
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddMedicationUseCaseTest {

    private lateinit var addMedicationUseCase: AddMedicationUseCase
    private lateinit var userProvider: FakeUserProvider
    private lateinit var petProvider: FakePetProvider
    private lateinit var medicationRepository: FakeMedicationRepository
    private lateinit var medicationEventRepository: FakeMedicationEventRepository

    lateinit var petMemberRepository: FakePetMemberRepository
    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        petProvider = FakePetProvider()
        medicationRepository = FakeMedicationRepository()
        medicationEventRepository = FakeMedicationEventRepository()
        petMemberRepository = FakePetMemberRepository()
        addMedicationUseCase = AddMedicationUseCase(
            userProvider,
            petProvider,
            medicationRepository,
            medicationEventRepository,
            petMemberRepository,
        )
    }

    @Test
    fun `invoke should return Success when user and pet are set and repositories succeed`() = runTest {
        // Given
        userProvider.setUserId("user123")
        petProvider.setCurrentPetId("pet123")
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        // When
        val result = addMedicationUseCase(
            name = "Aspirin",
            form = "Pill",
            dose = "10mg",
            notes = "Take with food",
            from = today,
            to = today,
            reccurenceString = "FREQ=DAILY",
            times = emptyList()
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result[1] is Resource.Success)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()
        // petProvider might be set or not, shouldn't matter if user check is first
        petProvider.setCurrentPetId("pet123")
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        // When
        val result = addMedicationUseCase(
            name = "Aspirin",
            form = "Pill",
            dose = "10mg",
            notes = "Take with food",
            from = today,
            to = today,
            reccurenceString = "FREQ=DAILY",
            times = emptyList()
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
        assertEquals("User not logged in", (result[1] as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when no pet is selected`() = runTest {
        // Given
        userProvider.setUserId("user123")
        petProvider.setCurrentPetId(null)
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        // When
        val result = addMedicationUseCase(
            name = "Aspirin",
            form = "Pill",
            dose = "10mg",
            notes = "Take with food",
            from = today,
            to = today,
            reccurenceString = "FREQ=DAILY",
            times = emptyList()
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result[1] is Resource.Error)

        assertEquals("No pet selected", (result[1] as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when repository throws Failure`() = runTest {
        // Given
        userProvider.setUserId("user123")
        petProvider.setCurrentPetId("pet123")
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        // Triggering a specific error in FakeMedicationRepository based on name
        val errorName = "Server error"

        // When
        val result = addMedicationUseCase(
            name = errorName,
            form = "Pill",
            dose = "10mg",
            notes = "Take with food",
            from = today,
            to = today,
            reccurenceString = "FREQ=DAILY",
            times = emptyList()
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
        assertEquals("Simulated server error", (result[1] as Resource.Error).message)
    }

    @Test
    fun `user is not a pet member should return Error`() = runTest {
        // Given
        userProvider.setUserId("user1235")
        petProvider.setCurrentPetId("pet123")
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        // When
        val result = addMedicationUseCase(
            name = "Aspirin",
            form = "Pill",
            dose = "10mg",
            notes = "Take with food",
            from = today,
            to = today,
            reccurenceString = "FREQ=DAILY",
            times = emptyList()
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertEquals("User does not have permission to add medication for this pet", (result[1] as Resource.Error).message)
    }
}

