package com.example.petcare.domain.use_case.add_pet

import com.example.petcare.common.Resource
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.data.fake_providers.FakePetProvider
import com.example.petcare.data.fake_providers.FakeUserProvider
import com.example.petcare.data.fake_repos.FakePetMemberRepository
import com.example.petcare.data.fake_repos.FakePetRepository
import com.example.petcare.data.fake_repos.FakeUserRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddPetUseCaseTest {

    private lateinit var addPetUseCase: AddPetUseCase
    private lateinit var userProvider: FakeUserProvider
    private lateinit var petProvider: FakePetProvider
    private lateinit var petRepository: FakePetRepository
    private lateinit var petMemberRepository: FakePetMemberRepository
    private lateinit var userRepository: FakeUserRepository

    @Before
    fun setUp() {
        userProvider = FakeUserProvider()
        petProvider = FakePetProvider()
        petRepository = FakePetRepository()
        petMemberRepository = FakePetMemberRepository()
        userRepository = FakeUserRepository()

        addPetUseCase = AddPetUseCase(
            userProvider,
            petProvider,
            petRepository,
            petMemberRepository,
            userRepository
        )
    }

    @Test
    fun `invoke should return Success when user is logged in and repositories succeed`() = runTest {
        // Given
        userProvider.setUserId("user123")
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        // When
        val result = addPetUseCase(
            name = "Buddy",
            species = speciesEnum.dog,
            breed = "Golden Retriever",
            birthDate = today,
            sex = sexEnum.male,
            byteArrayImage = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Success)
    }

    @Test
    fun `invoke should return Error when user is not logged in`() = runTest {
        // Given
        userProvider.clearUserData()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        // When
        val result = addPetUseCase(
            name = "Buddy",
            species = speciesEnum.dog,
            breed = "Golden Retriever",
            birthDate = today,
            sex = sexEnum.male,
            byteArrayImage = null
        ).toList()

        // Then
        assertTrue(result.first() is Resource.Loading)
        assertTrue(result.last() is Resource.Error)
        assertEquals("User not logged in", (result.last() as Resource.Error).message)
    }
}
