package com.example.petcare.domain.use_case.generate_pet_share_code

import com.example.petcare.common.Resource
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakePetRepository
import com.example.petcare.data.fake_repos.FakePetShareCodeRepository
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.use_case.add_pet_by_key.AddPetByKeyUseCase
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GeneratePetShareCodeUseCaseTest {

    private lateinit var generatePetShareCodeUseCase: GeneratePetShareCodeUseCase
    private lateinit var addPetByKeyUseCase : AddPetByKeyUseCase
    private lateinit var userProvider: FakeUserProvider
    private lateinit var petProvider: FakePetProvider
    private lateinit var petRepository: FakePetRepository
    private lateinit var petShareCodeRepository: FakePetShareCodeRepository
    private lateinit var petMemberRepository: FakePetMemberRepository


    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        petProvider = FakePetProvider()
        petRepository = FakePetRepository()
        petShareCodeRepository = FakePetShareCodeRepository()
        petMemberRepository = FakePetMemberRepository()
        generatePetShareCodeUseCase = GeneratePetShareCodeUseCase(
            userProvider,
            petProvider,
            petRepository,
            petShareCodeRepository
        )
        addPetByKeyUseCase = AddPetByKeyUseCase(
            userProvider = userProvider,
            petProvider = petProvider,
            petShareCodeRepository = petShareCodeRepository,
            petMememberRepository = petMemberRepository
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
        val result = generatePetShareCodeUseCase(petId).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
        val shareCode = (result.last() as Resource.Success).data
        assertEquals(petId, shareCode?.petId)
        assertEquals(8, shareCode?.code?.length)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()
        val petId = "pet123"

        // When
        val result = generatePetShareCodeUseCase(petId).toList()

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
        val result = generatePetShareCodeUseCase(petId).toList()

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
        val result = generatePetShareCodeUseCase(petId).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("You are not the owner of this pet", (result.last() as Resource.Error).message)
    }

    @Test
    fun `add pet by key use case full flow`() = runTest {
        val userId = "user1243"
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
        );
        petRepository.createPet(pet, null);
        val shareCodeFlow = generatePetShareCodeUseCase(petId).toList()
        val shareCode = (shareCodeFlow.last() as Resource.Success).data
        addPetByKeyUseCase(shareCode!!.code).toList()
        val isMember = petMemberRepository.isUserPetMember(
            userId = userId,
            petId = petId
        )
        assertTrue(isMember)
        assertTrue(isMember)

    }
}

