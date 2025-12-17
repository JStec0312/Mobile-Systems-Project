package com.example.petcare.domain.use_case.add_pet_by_key

import com.example.petcare.common.Resource
import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakePetShareCodeRepository
import com.example.petcare.domain.model.PetShareCode
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class AddPetByKeyUseCaseTest {

    private lateinit var addPetByKeyUseCase: AddPetByKeyUseCase
    private lateinit var userProvider: FakeUserProvider
    private lateinit var petProvider: FakePetProvider
    private lateinit var petShareCodeRepository: FakePetShareCodeRepository
    private lateinit var petMemberRepository: FakePetMemberRepository

    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        petProvider = FakePetProvider()
        petShareCodeRepository = FakePetShareCodeRepository()
        petMemberRepository = FakePetMemberRepository()

        addPetByKeyUseCase = AddPetByKeyUseCase(
            userProvider,
            petProvider,
            petShareCodeRepository,
            petMemberRepository
        )
    }

    @Test
    fun `invoke should return Success when valid key is provided and user is logged in`() = runTest {
        // Given
        userProvider.setUserId("user123")
        val validKey = "VALID_KEY"
        val petId = "pet123"
        val futureDate = Clock.System.now().plus(1, DateTimeUnit.DAY, TimeZone.UTC)
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        petShareCodeRepository.createPetShareCode(
            PetShareCode(
                id = UUID.randomUUID().toString(),
                petId = petId,
                code = validKey,
                expiresAt = futureDate,
                createdAt = today
            )
        )

        // When
        val result = addPetByKeyUseCase(validKey).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()
        val validKey = "VALID_KEY"

        // When
        val result = addPetByKeyUseCase(validKey).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User not logged in", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when key is invalid`() = runTest {
        // Given
        userProvider.setUserId("user123")
        val invalidKey = "INVALID_KEY"

        // When
        val result = addPetByKeyUseCase(invalidKey).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("Invalid pet key", (result.last() as Resource.Error).message)
    }

    @Test
    fun `invoke should return Error when key is expired`() = runTest {
        // Given
        userProvider.setUserId("user123")
        val expiredKey = "EXPIRED_KEY"
        val petId = "pet123"
        val pastDate = Clock.System.now().plus(-1, DateTimeUnit.DAY, TimeZone.UTC)
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        petShareCodeRepository.createPetShareCode(
            PetShareCode(
                id = UUID.randomUUID().toString(),
                petId = petId,
                code = expiredKey,
                expiresAt = pastDate,
                createdAt = today
            )
        )

        // When
        val result = addPetByKeyUseCase(expiredKey).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("Pet key has expired", (result.last() as Resource.Error).message)
    }
}
