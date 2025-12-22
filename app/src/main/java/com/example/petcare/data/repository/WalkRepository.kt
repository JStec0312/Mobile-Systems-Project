package com.example.petcare.data.repository

import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.Walk
import com.example.petcare.domain.repository.IWalkRepository
import com.example.petcare.exceptions.GeneralFailure
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Instant

class WalkRepository(auth: FirebaseAuth, db: FirebaseFirestore) : IWalkRepository{
    private final val col = db.collection(FirestorePaths.WALKS)
    override suspend fun createWalk(walk: Walk) {
        try{
            val dto = walk.toFirestoreDto();
            col.document(walk.id).set(dto).await();
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "createWalk");
        }
    }

    override suspend fun setWalkAsEnded(
        walkId: String,
        totalDistanceMeters: Float,
        totalSteps: Int,
        endTime: Instant
    ) {
        try{
            val walkDocRef = col.document(walkId)
            val walkSnapshot = walkDocRef.get().await()
            if (!walkSnapshot.exists()){
                throw GeneralFailure.WalkNotFound();
            }
            val updatedWalk = walkSnapshot.toObject(com.example.petcare.data.dto.firestore.WalkFirestoreDto::class.java);
            if (updatedWalk == null){
                throw GeneralFailure.DataCorruption("Walk data is null");
            }
            val endWalk = updatedWalk.copy(
                endedAt = endTime.toFirebaseTimestamp(),
                distanceMeters = totalDistanceMeters.toInt(),
                steps = totalSteps,
                pending = false,
                durationSec = ((endTime.epochSeconds) - (updatedWalk!!.startedAt.seconds)).toInt()
            )
            col.document(walkId).set(endWalk).await();
        } catch (t: Throwable){
            throw FirestoreThrowable.map(t, "setWalkAsEnded");
        }
    }


}