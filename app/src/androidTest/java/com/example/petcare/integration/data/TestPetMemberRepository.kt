package com.example.petcare.integration.data

import com.example.petcare.data.repository.FirestorePaths
import com.example.petcare.domain.model.PetMember
import com.example.petcare.domain.repository.IPetMemberRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.LocalDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class TestPetMemberRepository {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @Inject lateinit var firestore: FirebaseFirestore
    @Inject lateinit var petMemberRepo: IPetMemberRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }


    @Test
    fun test_add_pet_member() = runBlocking {
        val petMember = PetMember(
            id = UUID.randomUUID().toString(),
            petId = "testPetId",
            userId = "testUserId",
            createdAt = LocalDate(2025, 12, 20)
        )
        petMemberRepo.addPetMember(petMember)

        val isMember = petMemberRepo.isUserPetMember("testUserId", "testPetId")
        assertTrue(isMember)
    }

    @Test
    fun test_get_pet_ids_by_user_id() = runBlocking {
        val userId = "testUserId"
        val petId1 = "testPetId1"
        val petId2 = "testPetId2"

        val petMember1 = PetMember(
            id = UUID.randomUUID().toString(),
            petId = petId1,
            userId = userId,
            createdAt = LocalDate(2025, 12, 20)
        )
        val petMember2 = PetMember(
            id = UUID.randomUUID().toString(),
            petId = petId2,
            userId = userId,
            createdAt = LocalDate(2025, 12, 20)
        )

        petMemberRepo.addPetMember(petMember1)
        petMemberRepo.addPetMember(petMember2)

        val petIds = petMemberRepo.getPetIdsByUserId(userId)
        assertEquals(2, petIds.size)
        assertTrue(petIds.contains(petId1))
        assertTrue(petIds.contains(petId2))
    }

    @Test
    fun test_is_user_pet_member() = runBlocking {
        val userId = "testUserId"
        val petId = "testPetId"
        val otherUserId = "otherUserId"

        val petMember = PetMember(
            id = UUID.randomUUID().toString(),
            petId = petId,
            userId = userId,
            createdAt = LocalDate(2025, 12, 20)
        )
        petMemberRepo.addPetMember(petMember)

        assertTrue(petMemberRepo.isUserPetMember(userId, petId))
        assertFalse(petMemberRepo.isUserPetMember(otherUserId, petId))
        assertFalse(petMemberRepo.isUserPetMember(userId, "otherPetId"))
    }
}

