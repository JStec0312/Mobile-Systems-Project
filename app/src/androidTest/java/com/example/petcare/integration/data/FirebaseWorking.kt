package com.example.petcare.integration.data

import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class FirestoreEmulatorSmokeTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var firestore: FirebaseFirestore
    @Before
    fun setup() {
        hiltRule.inject()
    }
    @Test
    fun firestore_emulator_can_write_and_read() = runBlocking {
        val docId = UUID.randomUUID().toString()

        val docRef = firestore
            .collection("integration_tests")
            .document(docId)

        val payload = mapOf(
            "hello" to "world",
            "n" to 123
        )

        docRef.set(payload).await()

        val snap = docRef.get().await()
        assertTrue(snap.exists())
        assertEquals("world", snap.getString("hello"))
        assertEquals(123L, snap.getLong("n"))
    }
}