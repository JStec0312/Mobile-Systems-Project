package com.example.petcare.integration.data

import com.example.petcare.data.repository.PetShareCodeRepository
import com.example.petcare.data.repository.FirestorePaths
import com.example.petcare.domain.model.PetShareCode
import com.example.petcare.integration.data.test_utils.FirestoreTestUtils
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

import javax.inject.Inject

@HiltAndroidTest
class TestPetShareCodeRepository {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var firestore: FirebaseFirestore

    lateinit var repo: PetShareCodeRepository

    @Before
    fun setup(){
        hiltRule.inject()
        repo = PetShareCodeRepository(firestore)
    }

    @Test
    fun create_and_get_by_value() = runBlocking {
        val id = UUID.randomUUID().toString()
        val code = "TESTCODE-${UUID.randomUUID()}"
        val now = Clock.System.now()
        val ps = PetShareCode(
            id = id,
            petId = "pet-1",
            code = code,
            createdAt = LocalDate(2025, 12, 21),
            expiresAt = now
        )
        val created = repo.createPetShareCode(ps)
        assertEquals(ps.id, created.id)

        val fetched = repo.getPetShareCodeByValue(code)
        assertNotNull(fetched)
        assertEquals(ps.id, fetched!!.id)
        assertEquals(ps.code, fetched.code)
    }

    @Test
    fun get_by_value_returns_null_when_missing() = runBlocking {
        val fetched = repo.getPetShareCodeByValue("no-such-code-${UUID.randomUUID()}")
        assertNull(fetched)
    }

    @Test
    fun deletePetShareCodeById_removes_document() = runBlocking {
        val id = UUID.randomUUID().toString()
        val code = "DEL-${UUID.randomUUID()}"
        val ps = PetShareCode(
            id = id,
            petId = "pet-del",
            code = code,
            createdAt = LocalDate(2025,12,21),
            expiresAt = Clock.System.now()
        )
        repo.createPetShareCode(ps)
        // ensure present
        val fetched = repo.getPetShareCodeByValue(code)
        assertNotNull(fetched)

        repo.deletePetShareCodeById(id)
        val after = repo.getPetShareCodeByValue(code)
        assertNull(after)

        // direct firestore check
        val doc = firestore.collection(FirestorePaths.PET_SHARE_CODES).document(id).get().await()
        assertFalse(doc.exists())
    }
}

