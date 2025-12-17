package com.example.petcare.domain.use_case.delete_medication

import com.example.petcare.common.Resource
import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakeMedicationRepository
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class DeleteMedicationUseCaseTest {

    private lateinit var deleteMedicationUseCase: DeleteMedicationUseCase
    private lateinit var userProvider: IUserProvider
    private lateinit var petProvider: IPetProvider
    private lateinit var medicationRepository: IMedicationRepository
    private lateinit var petMemberRepository: IPetMemberRepository

    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        petProvider = FakePetProvider()
        medicationRepository = FakeMedicationRepository()
        petMemberRepository = FakePetMemberRepository()
        deleteMedicationUseCase = DeleteMedicationUseCase(
            userProvider,
            petProvider,
            medicationRepository,
            petMemberRepository
        )
    }

    @Test
    fun `invoke should return Success when user is logged in and is a member of the pet`() = runTest {
        // Given
        val userId = "user123"
        val petId = "pet123"
        val medicationId = UUID.randomUUID().toString()
        val medication = Medication(
            id = medicationId,
            petId = petId,
            name = "Aspirin",
            form = "Pill",
            dose = "1 pill",
            notes = "With food",
            active = true,
            createdAt = com.example.petcare.common.utils.DateConverter.localDateNow(),
            from = com.example.petcare.common.utils.DateConverter.localDateNow(),
            to = com.example.petcare.common.utils.DateConverter.localDateNow(),
            reccurenceString = "FREQ:DAILY",
            times = listOf()
        )

        (userProvider as FakeUserProvider).setUserId(userId)
        medicationRepository.createMedication(medication)
        // Note: FakePetMemberRepository already has a member with userId="user123" and petId="pet123" in its init block

        // When
        val result = deleteMedicationUseCase(medicationId).toList()

        // Then
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Success)

        // Verify medication is deleted
        try {
            medicationRepository.getMedicationById(medicationId)
            assertTrue("Medication should have been deleted", false)
        } catch (e: Exception) {
            // Expected exception
        }
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        val medicationId = UUID.randomUUID().toString()
        (userProvider as FakeUserProvider).clearUserData()

        // When
        val result = deleteMedicationUseCase(medicationId).toList()

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
        val medicationId = UUID.randomUUID().toString()
        val medication = Medication(
            id = medicationId,
            petId = petId,
            name = "Aspirin",
            form = "Pill",
            dose = "1 pill",
            notes = "With food",
            active = true,
            createdAt = com.example.petcare.common.utils.DateConverter.localDateNow(),
            from = com.example.petcare.common.utils.DateConverter.localDateNow(),
            to = com.example.petcare.common.utils.DateConverter.localDateNow(),
            reccurenceString = "FREQ:DAILY",
            times = listOf()
        )

        (userProvider as FakeUserProvider).setUserId(userId)
        medicationRepository.createMedication(medication)

        // When
        val result = deleteMedicationUseCase(medicationId).toList()

        // Then
        assertTrue(result[0] is Resource.Loading)
        assertTrue(result[1] is Resource.Error)
        assertEquals("User does not have permission to delete medication for this pet", (result[1] as Resource.Error).message)
    }
}
