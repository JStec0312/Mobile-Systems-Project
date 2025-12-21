package com.example.petcare.integration.data

import com.example.petcare.data.repository.FirestorePaths
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

import javax.inject.Inject

@HiltAndroidTest
class TestMedicationEventsRepository {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    @Inject lateinit var firestore: FirebaseFirestore
    @Inject lateinit var medicationEventRepo : IMedicationEventRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }



    @Test
    fun test_create_medication() = runBlocking{
        val testMedication = Medication(
            id = UUID.randomUUID().toString(),
            petId = "testPetId",
            name = "Test Medication",
            form = "Pill",
            dose = "50mg",
            notes = "Take with food",
            active = true,
            createdAt = LocalDate(2025, 12, 11),
            from = LocalDate(2025, 12, 12),
            to = LocalDate(2025, 12, 20),
            reccurenceString = "FREQ=DAILY;INTERVAL=1",
            times = listOf(
                kotlinx.datetime.LocalTime(8, 0),
                kotlinx.datetime.LocalTime(20, 0)
            )
        )
        medicationEventRepo.createByMedication(testMedication);
        val col = firestore.collection(FirestorePaths.MEDICATION_EVENTS)
        val querySnap = col.whereEqualTo("medicationId", testMedication.id).get().await()
        assert(querySnap.size() == 18) // 9 days * 2 times per day-

    }
    @Test
    fun getUpcomingForUser() = runBlocking{
        val testMedication = Medication(
            id = UUID.randomUUID().toString(),
            petId = "testPetId",
            name = "Test Medication",
            form = "Pill",
            dose = "50mg",
            notes = "Take with food",
            active = true,
            createdAt = LocalDate(2025, 12, 11),
            from = LocalDate(2025, 12, 15),
            to = LocalDate(2025, 12, 20),
            reccurenceString = "FREQ=DAILY;INTERVAL=1",
            times = listOf(
                kotlinx.datetime.LocalTime(8, 0),
                kotlinx.datetime.LocalTime(20, 0)
            )
        )
        val testMedication2 = Medication(
            id = UUID.randomUUID().toString(),
            petId = "testPetId2",
            name = "Test Medication 2",
            form = "Pill",
            dose = "100mg",
            notes = "Take with water",
            active = true,
            createdAt = LocalDate(2025, 12, 11),
            from = LocalDate(2025, 12, 15),
            to = LocalDate(2025, 12, 18),
            reccurenceString = "FREQ=DAILY;INTERVAL=2",
            times = listOf(
                kotlinx.datetime.LocalTime(9, 0),

            )
        )
        val from = LocalDate(2025, 12, 15).atStartOfDayIn(TimeZone.of("Europe/Warsaw"))
        val to = LocalDateTime(2025, 12, 20, 23, 59, 59).toInstant(TimeZone.of("Europe/Warsaw"))
        medicationEventRepo.createByMedication(testMedication);
        medicationEventRepo.createByMedication(testMedication2);
        val repoResult = medicationEventRepo.getUpcomingMedicationEventsForUserInDateRange(listOf("testPetId", "testPetId2"),
            from,
            to
        )
        assertEquals("Actual size is: ${repoResult.size}",repoResult.size, 14 ) // 6 from first medication, 1 from second medication

    }

}