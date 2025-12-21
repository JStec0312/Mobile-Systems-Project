package com.example.petcare.integration.data

import com.example.petcare.data.dto.firestore.MedicationFirestoreDto
import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.data.repository.FirestorePaths
import com.example.petcare.data.repository.MedicationRepository
import com.example.petcare.domain.model.Medication
import com.example.petcare.integration.data.test_utils.FirestoreTestUtils
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
class TestMedicationRepository {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var firestore: FirebaseFirestore

    lateinit var medicationRepo: MedicationRepository

    @Before
    fun setup() {
        hiltRule.inject()
        medicationRepo = MedicationRepository(firestore)
        // clear collections before each test
    }

    @Test
    fun create_and_get_medication() = runBlocking {
        val med = Medication(
            id = UUID.randomUUID().toString(),
            petId = "pet-1",
            name = "TestMed",
            form = "Pill",
            dose = "10mg",
            notes = "notes",
            active = true,
            createdAt = LocalDate(2025, 12, 12),
            from = LocalDate(2025, 12, 13),
            to = LocalDate(2025, 12, 20),
            reccurenceString = "FREQ=DAILY;INTERVAL=1",
            times = emptyList()
        )
        medicationRepo.createMedication(med)
        // verify directly in firestore
        val doc = firestore.collection(FirestorePaths.MEDICATIONS).document(med.id).get().await()
        assertTrue(doc.exists())
        val fetched = medicationRepo.getMedicationById(med.id)
        assertEquals(med.id, fetched.id)
        assertEquals(med.name, fetched.name)
    }

    @Test
    fun listMedicationsForPet_returns_items() = runBlocking {
        val petId = "pet-list-1"
        val med1 = Medication(
            id = UUID.randomUUID().toString(),
            petId = petId,
            name = "Med1",
            form = "Pill",
            dose = "10mg",
            notes = "",
            active = true,
            createdAt = LocalDate(2025, 12, 12),
            from = LocalDate(2025, 12, 13),
            to = LocalDate(2025, 12, 14),
            reccurenceString = "",
            times = emptyList()
        )
        val med2 = med1.copy(id = UUID.randomUUID().toString(), name = "Med2")
        medicationRepo.createMedication(med1)
        medicationRepo.createMedication(med2)

        val list = medicationRepo.listMedicationsForPet(petId)
        assertEquals(2, list.size)
        val names = list.map { it.name }
        assertTrue(names.containsAll(listOf("Med1", "Med2")))
    }



    @Test
    fun deleteMedication_works_and_then_not_found() = runBlocking {
        val med = Medication(
            id = UUID.randomUUID().toString(),
            petId = "pet-del-1",
            name = "ToDelete",
            form = "Pill",
            dose = "10mg",
            notes = "",
            active = true,
            createdAt = LocalDate(2025, 12, 12),
            from = LocalDate(2025, 12, 13),
            to = LocalDate(2025, 12, 14),
            reccurenceString = "",
            times = emptyList()
        )
        medicationRepo.createMedication(med)
        medicationRepo.deleteMedication(med.id)
        // ensure doc removed
        val doc = firestore.collection(FirestorePaths.MEDICATIONS).document(med.id).get().await()
        assertFalse(doc.exists())
        // get should throw
        try {
            medicationRepo.getMedicationById(med.id)
            fail("Expected getMedicationById to throw after deletion")
        } catch (e: Exception) {
            // expected
        }
    }
}

