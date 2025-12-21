package com.example.petcare.integration.data

import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.data.repository.FirestorePaths
import com.example.petcare.data.repository.PetRepository
import com.example.petcare.domain.model.Pet
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
class TestPetRepository {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var firestore: FirebaseFirestore

    lateinit var petRepo: PetRepository

    @Before
    fun setup() {
        hiltRule.inject()
        petRepo = PetRepository(firestore)
    }

    @Test
    fun create_and_get_pet() = runBlocking {
        val pet = Pet(
            id = UUID.randomUUID().toString(),
            ownerUserId = "owner-1",
            name = "Buddy",
            species = speciesEnum.dog,
            breed = "Mixed",
            sex = sexEnum.male,
            birthDate = LocalDate(2020, 1, 1),
            avatarThumbUrl = null,
            createdAt = LocalDate(2025, 12, 20)
        )
        val created = petRepo.createPet(pet, null)
        assertEquals(pet.id, created.id)

        val fetched = petRepo.getPetById(pet.id)
        assertEquals(pet.name, fetched.name)
        assertEquals(pet.ownerUserId, fetched.ownerUserId)
    }

    @Test
    fun getPetsByIds_returns_list() = runBlocking {
        val pet1 = Pet(
            id = UUID.randomUUID().toString(),
            ownerUserId = "owner-a",
            name = "PetA",
            species = speciesEnum.cat,
            breed = "",
            sex = sexEnum.unknown,
            birthDate = LocalDate(2021, 5, 5),
            avatarThumbUrl = null,
            createdAt = LocalDate(2025, 12, 20)
        )
        val pet2 = pet1.copy(id = UUID.randomUUID().toString(), name = "PetB", ownerUserId = "owner-b")
        petRepo.createPet(pet1, null)
        petRepo.createPet(pet2, null)

        val results = petRepo.getPetsByIds(listOf(pet1.id, pet2.id))
        assertEquals(2, results.size)
        val ids = results.map { it.id }
        assertTrue(ids.containsAll(listOf(pet1.id, pet2.id)))
    }

    @Test
    fun editPet_updates_fields() = runBlocking {
        val pet = Pet(
            id = UUID.randomUUID().toString(),
            ownerUserId = "owner-edit",
            name = "OldName",
            species = speciesEnum.dog,
            breed = "",
            sex = sexEnum.female,
            birthDate = LocalDate(2019, 3, 3),
            avatarThumbUrl = null,
            createdAt = LocalDate(2025, 12, 20)
        )
        petRepo.createPet(pet, null)
        val edited = pet.copy(name = "NewName", breed = "Beagle")
        val updated = petRepo.editPet(edited, null)
        assertEquals("NewName", updated.name)
        assertEquals("Beagle", updated.breed)

        val fetched = petRepo.getPetById(pet.id)
        assertEquals("NewName", fetched.name)
        assertEquals("Beagle", fetched.breed)
    }

    @Test
    fun deletePetById_removes_pet() = runBlocking {
        val pet = Pet(
            id = UUID.randomUUID().toString(),
            ownerUserId = "owner-del",
            name = "ToDelete",
            species = speciesEnum.dog,
            breed = null,
            sex = sexEnum.male,
            birthDate = LocalDate(2018, 7, 7),
            avatarThumbUrl = null,
            createdAt = LocalDate(2025, 12, 20)
        )
        petRepo.createPet(pet, null)
        petRepo.deletePetById(pet.id, pet.ownerUserId)

        val doc = firestore.collection(FirestorePaths.PETS).document(pet.id).get().await()
        assertFalse(doc.exists())
    }

    @Test(expected = Exception::class)
    fun getPetById_throws_when_not_found() = runBlocking<Unit> {
        petRepo.getPetById("no-such-pet")
    }
}

