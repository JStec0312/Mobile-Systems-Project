package com.example.petcare.domain.use_case.edit_pet

import com.example.petcare.common.Resource
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakePetRepository
import com.example.petcare.domain.model.Pet
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EditPetUseCaseTest {

    private lateinit var editPetUseCase: EditPetUseCase
    private lateinit var userProvider: FakeUserProvider
    private lateinit var petProvider: FakePetProvider
    private lateinit var petRepository: FakePetRepository

    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        petProvider = FakePetProvider()
        petRepository = FakePetRepository()

        editPetUseCase = EditPetUseCase(
            userProvider,
            petProvider,
            petRepository
        )
    }

    @Test
    fun `invoke should return Success when user is logged in and is owner of the pet`() = runTest {
        // Given
        val userId = "user123"
        val petId = "pet123"
        userProvider.setUserId(userId)

        // Create a pet owned by the user
        val pet = Pet(
            id = petId,
            ownerUserId = userId,
            name = "Buddy",
            species = speciesEnum.dog,
            breed = "Golden Retriever",
            birthDate = LocalDate(2020, 1, 1),
            sex = sexEnum.male,
            avatarThumbUrl = null,
            createdAt = LocalDate(2023, 1, 1)
        )
        petRepository.createPet(pet, null)

        // When
        val result = editPetUseCase(
            petId = petId,
            ownerUserId = userId,
            name = "Buddy Updated",
            species = null,
            breed = null,
            sex = null,
            birthDate = null,
            avatarThumbUrl = null,
            byteArrayImage = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val updatedPet = (result.last() as Resource.Success).data
        assertEquals("Buddy Updated", updatedPet?.name)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()
        val petId = "pet123"

        // When
        val result = editPetUseCase(
            petId = petId,
            ownerUserId = "user123",
            name = "Buddy Updated",
            species = null,
            breed = null,
            sex = null,
            birthDate = null,
            avatarThumbUrl = null,
            byteArrayImage = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User not logged in", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when pet not found`() = runTest {
        // Given
        val userId = "user123"
        userProvider.setUserId(userId)
        val petId = "nonExistentPet"

        // When
        val result = editPetUseCase(
            petId = petId,
            ownerUserId = userId,
            name = "Buddy Updated",
            species = null,
            breed = null,
            sex = null,
            birthDate = null,
            avatarThumbUrl = null,
            byteArrayImage = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("Pet not found", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when user is not owner of the pet`() = runTest {
        // Given
        val userId = "user123"
        val otherUserId = "otherUser"
        val petId = "pet123"
        userProvider.setUserId(userId)

        // Create a pet owned by another user
        val pet = Pet(
            id = petId,
            ownerUserId = otherUserId,
            name = "Buddy",
            species = speciesEnum.dog,
            breed = "Golden Retriever",
            birthDate = LocalDate(2020, 1, 1),
            sex = sexEnum.male,
            avatarThumbUrl = null,
            createdAt = LocalDate(2023, 1, 1)
        )
        petRepository.createPet(pet, null)

        // When
        val result = editPetUseCase(
            petId = petId,
            ownerUserId = otherUserId,
            name = "Buddy Updated",
            species = null,
            breed = null,
            sex = null,
            birthDate = null,
            avatarThumbUrl = null,
            byteArrayImage = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("You are not an owner of the pet", (result.last() as Resource.Error).message)
    }
}

