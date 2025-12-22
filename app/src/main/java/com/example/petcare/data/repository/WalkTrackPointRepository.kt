package com.example.petcare.data.repository

import com.example.petcare.data.dto.firestore.WalkTrackPointFirestoreDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.WalkTrackPoint
import com.example.petcare.domain.repository.IWalkTrackPointRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class WalkTrackPointRepository @Inject constructor(
    private val db: FirebaseFirestore
) : IWalkTrackPointRepository {

    private fun trackPointsCol(walkId: String) =
        db.collection(FirestorePaths.WALKS)
            .document(walkId)
            .collection(FirestorePaths.WALK_TRACK_POINTS)

    override suspend fun addWalkTrackPoint(walkTrackPoint: WalkTrackPoint) {
        try {
            val col = trackPointsCol(walkTrackPoint.walkId!!)

            // jesli id jest puste -> generujemy; w przeciwnym razie uzywamy podanego
            val id = if (walkTrackPoint.id.isBlank()) {
                col.document().id
            } else {
                walkTrackPoint.id
            }

            val dto = walkTrackPoint.copy(id = id).toFirestoreDto()

            col.document(id)
                .set(dto)
                .await()
        } catch (t: Throwable) {
            Timber.d(t, "addWalkTrackPoint failed")
            throw FirestoreThrowable.map(t, "addWalkTrackPoint")
        }
    }

    override  fun observeWalkPoints(walkId: String): Flow<List<WalkTrackPoint>> {
        val col = trackPointsCol(walkId)

        return callbackFlow {
            var registration: ListenerRegistration? = null

            try {
                registration = col
                    .orderBy(WalkTrackPointFirestoreDto.FIELD_TS)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Timber.d(error, "observeWalkPoints snapshot error")
                            close(FirestoreThrowable.map(error, "observeWalkPoints"))
                            return@addSnapshotListener
                        }

                        if (snapshot == null) {
                            trySend(emptyList()).isSuccess
                            return@addSnapshotListener
                        }

                        val points = snapshot.documents.mapNotNull { doc ->
                            val dto = doc.toObject(WalkTrackPointFirestoreDto::class.java)
                            dto?.toDomain()
                        }

                        trySend(points).isSuccess
                    }
            } catch (t: Throwable) {
                close(FirestoreThrowable.map(t, "observeWalkPoints"))
            }

            awaitClose {
                registration?.remove()
            }
        }
    }
}
