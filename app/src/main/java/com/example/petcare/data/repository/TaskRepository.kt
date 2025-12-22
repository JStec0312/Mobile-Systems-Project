package com.example.petcare.data.repository

import com.example.petcare.common.taskStatusEnum
import com.example.petcare.data.dto.firestore.TaskDeletedOccurrenceFirestoreDto
import com.example.petcare.data.dto.firestore.TaskFirestoreDto
import com.example.petcare.data.dto.firestore.TaskOccurrenceOverrideFirestoreDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toFirestoreDto
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.exceptions.GeneralFailure
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import timber.log.Timber
import java.util.UUID
import java.util.TimeZone

private const val RECURRENCE_WINDOW_DAYS = 60L
private const val DAY_MS = 24L * 60 * 60 * 1000L

private data class OccKey(val seriesId: String, val occurrenceAtMillis: Long)

class TaskRepository(
    private val db: FirebaseFirestore
) : ITaskRepository {

    private val tasksCol = db.collection(FirestorePaths.TASKS)
    private val overridesCol = db.collection(FirestorePaths.TASK_OCCURRENCE_OVERRIDES)
    private val deletedCol = db.collection(FirestorePaths.TASK_DELETED_OCCURRENCES)

    override suspend fun createTask(task: Task, rrule: String?) {
        try {
            if (rrule != null) {
                try {
                    RecurrenceRule(rrule)
                } catch (_: Exception) {
                    throw GeneralFailure.InvalidIntervalPassed()
                }
            }

            val id = task.id.ifBlank { tasksCol.document().id }
            val seriesId = if (rrule != null) (task.seriesId ?: UUID.randomUUID().toString()) else null

            val dto = task.copy(
                id = id,
                seriesId = seriesId,
                rrule = rrule ?: task.rrule
            ).toFirestoreDto(rruleOverride = rrule)

            tasksCol.document(id).set(dto).await()
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "createTask")
        }
    }

    override suspend fun getTasksByPetId(petId: String): List<Task> {
        val nowMs = Clock.System.now().toEpochMilliseconds() - DAY_MS
        val from = Instant.fromEpochMilliseconds(nowMs)
        val to = Instant.fromEpochMilliseconds(nowMs + RECURRENCE_WINDOW_DAYS * DAY_MS)
        return getTasksByPetIdsInDateRange(listOf(petId), from, to)
    }

    override suspend fun getTasksByPetIds(petIds: List<String>): List<Task> {
        val nowMs = Clock.System.now().toEpochMilliseconds() - DAY_MS
        val from = Instant.fromEpochMilliseconds(nowMs)
        val to = Instant.fromEpochMilliseconds(nowMs + RECURRENCE_WINDOW_DAYS * DAY_MS)
        return getTasksByPetIdsInDateRange(petIds, from, to)
    }

    override suspend fun getTasksByPetIdsInDateRange(
        petIds: List<String>,
        from: Instant,
        to: Instant
    ): List<Task> {
        if (petIds.isEmpty()) return emptyList()
        require(from <= to) { "from must be <= to" }

        try {
            val fromMs = from.toEpochMilliseconds()
            val toMs = to.toEpochMilliseconds()

            val petChunks = petIds.chunked(10)

            val baseTasks = mutableListOf<Task>()
            for (chunk in petChunks) {
                // non-recurring in range
                val nonRecurringSnap = tasksCol
                    .whereIn(TaskFirestoreDto.FIELD_PET_ID, chunk)
                    .whereEqualTo(TaskFirestoreDto.FIELD_IS_RECURRING, false)
                    .whereGreaterThanOrEqualTo(TaskFirestoreDto.FIELD_DATE, from.toFirebaseTimestamp())
                    .whereLessThanOrEqualTo(TaskFirestoreDto.FIELD_DATE, to.toFirebaseTimestamp())
                    .get()
                    .await()
                Timber.d("Tasks non-recurring for chunk=$chunk size=${nonRecurringSnap.size()}")

                for (doc in nonRecurringSnap.documents) {
                    val dto = doc.toObject(TaskFirestoreDto::class.java)
                    if (dto != null) baseTasks.add(dto.toDomain(doc.id))
                }

                // recurring that may generate occurrences in range: start <= to
                val recurringSnap = tasksCol
                    .whereIn(TaskFirestoreDto.FIELD_PET_ID, chunk)
                    .whereEqualTo(TaskFirestoreDto.FIELD_IS_RECURRING, true)
                    .whereLessThanOrEqualTo(TaskFirestoreDto.FIELD_DATE, to.toFirebaseTimestamp())
                    .get()
                    .await()

                Timber.d("Tasks recurring for chunk=$chunk size=${recurringSnap.size()}")


                for (doc in recurringSnap.documents) {
                    val dto = doc.toObject(TaskFirestoreDto::class.java)
                    if (dto != null) baseTasks.add(dto.toDomain(doc.id))
                }
            }

            val recurring = baseTasks.filter { it.rrule != null && it.seriesId != null }
            val seriesIds = recurring.mapNotNull { it.seriesId }.distinct()
            val overrides = loadOverrides(seriesIds, fromMs, toMs)
            val deleted = loadDeleted(seriesIds, fromMs, toMs)

            val out = mutableListOf<Task>()

            for (task in baseTasks) {
                val rrule = task.rrule
                val seriesId = task.seriesId

                if (rrule == null || seriesId == null) {
                    // non recurring already filtered, but keep safe:
                    if (task.date in from..to) out.add(task)
                    continue
                }

                // expand RRULE like in FakeTaskRepository :contentReference[oaicite:2]{index=2}
                val rule = try { RecurrenceRule(rrule) } catch (_: Exception) {
                    if (task.date in from..to) out.add(task)
                    continue
                }

                val startMillis = task.date.toEpochMilliseconds()
                val startDateTime = DateTime(TimeZone.getDefault(), startMillis)
                val it = rule.iterator(startDateTime)

                while (it.hasNext()) {
                    val next = it.next()
                    val ts = next.timestamp

                    if (ts > toMs) break
                    if (ts < fromMs) continue

                    val key = OccKey(seriesId, ts)
                    if (deleted.contains(key)) continue

                    val status = overrides[key] ?: task.status

                    out.add(
                        task.copy(
                            id = buildOccurrenceId(task.id, ts),
                            date = Instant.fromEpochMilliseconds(ts),
                            status = status
                        )
                    )
                }
            }

            // opcjonalnie sort po dacie, zeby UI nie skakalo
            return out.sortedBy { it.date }

        } catch (t: Throwable) {
            Timber.d("getTasksByPetIdsInDateRange: $t")
            throw FirestoreThrowable.map(t, "getTasksByPetIdsInDateRange")
        }
    }

    override suspend fun updateTaskStatus(taskId: String, newStatus: taskStatusEnum) {
        try {
            val occ = parseOccurrenceId(taskId)
            if (occ == null) {
                // base task
                tasksCol.document(taskId)
                    .update(TaskFirestoreDto.FIELD_STATUS, newStatus.name)
                    .await()
                return
            }

            // occurrence override
            val (baseId, ts) = occ
            val baseDoc = tasksCol.document(baseId).get().await()
            val baseDto = baseDoc.toObject(TaskFirestoreDto::class.java) ?: throw GeneralFailure.TaskNotFound()
            val seriesId = baseDto.seriesId ?: throw GeneralFailure.TaskNotFound()

            val docPath = FirestorePaths.taskOccurrenceOverrideDoc(seriesId, ts)
            db.document(docPath)
                .set(
                    TaskOccurrenceOverrideFirestoreDto(
                        seriesId = seriesId,
                        occurrenceAtMillis = ts,
                        status = newStatus.name
                    )
                )
                .await()

        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "updateTaskStatus")
        }
    }

    override suspend fun getTaskById(taskId: String): Task {
        try {
            val occ = parseOccurrenceId(taskId)
            if (occ == null) {
                val doc = tasksCol.document(taskId).get().await()
                val dto = doc.toObject(TaskFirestoreDto::class.java) ?: throw GeneralFailure.TaskNotFound()
                return dto.toDomain(doc.id)
            }

            val (baseId, ts) = occ
            val baseDoc = tasksCol.document(baseId).get().await()
            val dto = baseDoc.toObject(TaskFirestoreDto::class.java) ?: throw GeneralFailure.TaskNotFound()
            val base = dto.toDomain(baseDoc.id)

            val seriesId = base.seriesId ?: throw GeneralFailure.TaskNotFound()

            // check deleted
            val deletedDoc = db.document(FirestorePaths.taskDeletedOccurrenceDoc(seriesId, ts)).get().await()
            if (deletedDoc.exists()) throw GeneralFailure.TaskNotFound()

            // override status if exists
            val overrideDoc = db.document(FirestorePaths.taskOccurrenceOverrideDoc(seriesId, ts)).get().await()
            val overrideDto = overrideDoc.toObject(TaskOccurrenceOverrideFirestoreDto::class.java)

            val status = overrideDto?.status?.let { taskStatusEnum.valueOf(it) } ?: base.status

            return base.copy(
                id = buildOccurrenceId(base.id, ts),
                date = Instant.fromEpochMilliseconds(ts),
                status = status
            )

        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "getTaskById")
        }
    }

    override suspend fun deleteTaskById(task: Task, deleteWholeSeries: Boolean) {
        try {
            if (deleteWholeSeries && task.seriesId != null) {
                deleteSeries(task.seriesId!!)
                return
            }

            val occ = parseOccurrenceId(task.id)
            if (occ == null) {
                // base doc delete
                tasksCol.document(task.id).delete().await()
                return
            }

            // mark single occurrence deleted
            val (baseId, ts) = occ
            val baseDoc = tasksCol.document(baseId).get().await()
            val baseDto = baseDoc.toObject(TaskFirestoreDto::class.java) ?: throw GeneralFailure.TaskNotFound()
            val seriesId = baseDto.seriesId ?: throw GeneralFailure.TaskNotFound()

            db.document(FirestorePaths.taskDeletedOccurrenceDoc(seriesId, ts))
                .set(TaskDeletedOccurrenceFirestoreDto(seriesId, ts))
                .await()

        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "deleteTaskById")
        }
    }

    override suspend fun updatateTask(task: Task, updateWholeSeries: Boolean) {
        try {
            // Fake repo nie ogarnial update pojedynczego occurrence (tylko base tasks) :contentReference[oaicite:3]{index=3}
            if (parseOccurrenceId(task.id) != null) {
                throw GeneralFailure.TaskNotFound()
            }

            if (updateWholeSeries && task.seriesId != null) {
                val snap = tasksCol.whereEqualTo(TaskFirestoreDto.FIELD_SERIES_ID, task.seriesId!!).get().await()
                if (snap.isEmpty) throw GeneralFailure.TaskNotFound()

                val batch = db.batch()
                for (doc in snap.documents) {
                    batch.set(doc.reference, task.toFirestoreDto(rruleOverride = task.rrule))
                }
                batch.commit().await()
            } else {
                tasksCol.document(task.id)
                    .set(task.toFirestoreDto(rruleOverride = task.rrule))
                    .await()
            }
        } catch (t: Throwable) {
            throw FirestoreThrowable.map(t, "updatateTask")
        }
    }

    // -------- helpers --------

    private fun buildOccurrenceId(baseId: String, ts: Long): String = "$baseId#$ts"

    private fun parseOccurrenceId(taskId: String): Pair<String, Long>? {
        val parts = taskId.split("#")
        if (parts.size != 2) return null
        val baseId = parts[0]
        val ts = parts[1].toLongOrNull() ?: return null
        return baseId to ts
    }

    private suspend fun loadOverrides(seriesIds: List<String>, fromMs: Long, toMs: Long): Map<OccKey, taskStatusEnum> {
        if (seriesIds.isEmpty()) return emptyMap()
        val out = mutableMapOf<OccKey, taskStatusEnum>()

        val chunks = seriesIds.chunked(10)
        for (chunk in chunks) {
            val snap = overridesCol
                .whereIn(TaskOccurrenceOverrideFirestoreDto.FIELD_SERIES_ID, chunk)
                .whereGreaterThanOrEqualTo(TaskOccurrenceOverrideFirestoreDto.FIELD_OCCURRENCE_AT_MILLIS, fromMs)
                .whereLessThanOrEqualTo(TaskOccurrenceOverrideFirestoreDto.FIELD_OCCURRENCE_AT_MILLIS, toMs)
                .get()
                .await()

            for (doc in snap.documents) {
                val dto = doc.toObject(TaskOccurrenceOverrideFirestoreDto::class.java) ?: continue
                val sid = dto.seriesId ?: continue
                val ts = dto.occurrenceAtMillis ?: continue
                val st = dto.status ?: continue
                out[OccKey(sid, ts)] = taskStatusEnum.valueOf(st)
            }
        }
        return out
    }

    private suspend fun loadDeleted(seriesIds: List<String>, fromMs: Long, toMs: Long): Set<OccKey> {
        if (seriesIds.isEmpty()) return emptySet()
        val out = mutableSetOf<OccKey>()

        val chunks = seriesIds.chunked(10)
        for (chunk in chunks) {
            val snap = deletedCol
                .whereIn(TaskDeletedOccurrenceFirestoreDto.FIELD_SERIES_ID, chunk)
                .whereGreaterThanOrEqualTo(TaskDeletedOccurrenceFirestoreDto.FIELD_OCCURRENCE_AT_MILLIS, fromMs)
                .whereLessThanOrEqualTo(TaskDeletedOccurrenceFirestoreDto.FIELD_OCCURRENCE_AT_MILLIS, toMs)
                .get()
                .await()

            for (doc in snap.documents) {
                val dto = doc.toObject(TaskDeletedOccurrenceFirestoreDto::class.java) ?: continue
                val sid = dto.seriesId ?: continue
                val ts = dto.occurrenceAtMillis ?: continue
                out.add(OccKey(sid, ts))
            }
        }
        return out
    }

    private suspend fun deleteSeries(seriesId: String) {
        // Uwaga: batch ma limit 500 operacji. Jak seria duza, trzeba porcjowac.
        // Na start: najprosciej usunac base taski + wszystkie override/deleted dokumenty po query.

        // delete tasks in series
        runPagedDelete(
            query = tasksCol.whereEqualTo(TaskFirestoreDto.FIELD_SERIES_ID, seriesId),
            label = "tasks"
        )

        runPagedDelete(
            query = overridesCol.whereEqualTo(TaskOccurrenceOverrideFirestoreDto.FIELD_SERIES_ID, seriesId),
            label = "overrides"
        )

        runPagedDelete(
            query = deletedCol.whereEqualTo(TaskDeletedOccurrenceFirestoreDto.FIELD_SERIES_ID, seriesId),
            label = "deleted"
        )
    }

    private suspend fun runPagedDelete(
        query: com.google.firebase.firestore.Query,
        label: String
    ) {
        var last: com.google.firebase.firestore.DocumentSnapshot? = null

        while (true) {
            var q = query.limit(400)
            if (last != null) q = q.startAfter(last!!)

            val snap = q.get().await()
            if (snap.isEmpty) break

            val batch = db.batch()
            for (doc in snap.documents) batch.delete(doc.reference)
            batch.commit().await()

            last = snap.documents.last()
        }
    }
}
