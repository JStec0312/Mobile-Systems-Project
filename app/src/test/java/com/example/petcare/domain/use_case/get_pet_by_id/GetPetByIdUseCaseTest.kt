package com.example.petcare.domain.use_case.get_pet_by_id

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

class GetPetByIdUseCaseTest {

    private lateinit var getPetByIdUseCase: GetPetByIdUseCase
    private lateinit var userProvider: FakeUserProvider
    private lateinit var petProvider: FakePetProvider
    private lateinit var petRepository: FakePetRepository

    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        petProvider = FakePetProvider()
        petRepository = FakePetRepository()

        getPetByIdUseCase = GetPetByIdUseCase(
            userProvider,
            petProvider,
            petRepository
        )
    }

    @Test
    fun `invoke should return Success when pet exists`() = runTest {
        // Given
        val userId = "user123"
        val petId = "pet123"
        userProvider.setUserId(userId)

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
        val result = getPetByIdUseCase(petId).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val retrievedPet = (result.last() as Resource.Success).data
        assertEquals(petId, retrievedPet?.id)
        assertEquals("Buddy", retrievedPet?.name)

        // Verify pet provider was updated
        assertEquals(petId, petProvider.getCurrentPetId())
    }

    @Test
    fun `invoke should return Error when pet not found`() = runTest {
        // Given
        val petId = "nonExistentPet"

        // When
        val result = getPetByIdUseCase(petId).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("Pet not found", (result.last() as Resource.Error).message)
    }
}

