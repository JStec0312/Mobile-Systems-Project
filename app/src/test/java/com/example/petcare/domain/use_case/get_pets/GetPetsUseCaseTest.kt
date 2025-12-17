package com.example.petcare.domain.use_case.get_pets

import com.example.petcare.common.Resource
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakePetRepository
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.model.PetMember
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class GetPetsUseCaseTest {

    private lateinit var getPetsUseCase: GetPetsUseCase
    private lateinit var userProvider: FakeUserProvider
    private lateinit var petRepository: FakePetRepository
    private lateinit var petMemberRepository: FakePetMemberRepository

    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        petRepository = FakePetRepository()
        petMemberRepository = FakePetMemberRepository()

        getPetsUseCase = GetPetsUseCase(
            petRepository,
            userProvider,
            petMemberRepository
        )
    }

    @Test
    fun `invoke should return Success with list of pets when user is logged in and has pets`() = runTest {
        // Given
        val userId = "user123"
        val petId1 = "pet1"
        val petId2 = "pet2"
        userProvider.setUserId(userId)

        // Create pets
        val pet1 = Pet(
            id = petId1,
            ownerUserId = userId,
            name = "Buddy",
            species = speciesEnum.dog,
            breed = "Golden Retriever",
            birthDate = LocalDate(2020, 1, 1),
            sex = sexEnum.male,
            avatarThumbUrl = null,
            createdAt = LocalDate(2023, 1, 1)
        )
        val pet2 = Pet(
            id = petId2,
            ownerUserId = userId,
            name = "Kitty",
            species = speciesEnum.cat,
            breed = "Siamese",
            birthDate = LocalDate(2021, 5, 10),
            sex = sexEnum.female,
            avatarThumbUrl = null,
            createdAt = LocalDate(2023, 2, 1)
        )
        petRepository.createPet(pet1, null)
        petRepository.createPet(pet2, null)

        // Add user as member of these pets
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        petMemberRepository.addPetMember(PetMember(UUID.randomUUID().toString(), petId1, userId, today))
        petMemberRepository.addPetMember(PetMember(UUID.randomUUID().toString(), petId2, userId, today))

        // When
        val result = getPetsUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val pets = (result.last() as Resource.Success).data
        assertEquals(2, pets?.size)
        assertTrue(pets?.any { it.id == petId1 } == true)
        assertTrue(pets?.any { it.id == petId2 } == true)
    }

    @Test
    fun `invoke should return Success with empty list when user has no pets`() = runTest {
        // Given
        val userId = "user123"
        userProvider.setUserId(userId)

        // When
        val result = getPetsUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val pets = (result.last() as Resource.Success).data
        assertTrue(pets?.isEmpty() == true)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()

        // When
        val result = getPetsUseCase().toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User not logged in", (result.last() as Resource.Error).message)
    }
}

