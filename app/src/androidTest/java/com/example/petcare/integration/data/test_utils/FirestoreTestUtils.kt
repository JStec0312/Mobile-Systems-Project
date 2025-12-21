package com.example.petcare.integration.data.test_utils

import com.example.petcare.data.repository.FirestorePaths
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking

object FirestoreTestUtils {
    /**
     * Clears test collections used by integration tests.
     * Currently removes documents from: medications, medicationEvents, pets
     */
    fun clearFirestoreData(firestore: FirebaseFirestore) = runBlocking {
        val collections = listOf(
            FirestorePaths.MEDICATIONS,
            FirestorePaths.MEDICATION_EVENTS,
            FirestorePaths.PETS
        )
        for (colName in collections) {
            val colRef = firestore.collection(colName)
            val docs = colRef.get().await().documents
            for (doc in docs) {
                colRef.document(doc.id).delete().await()
            }
        }
    }
}

