package com.example.petcare.integration.data

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.data.repository.FirestorePaths
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.repository.ITaskRepository
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
class TestTaskRepository {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var firestore: FirebaseFirestore
    @Inject lateinit var taskRepo: ITaskRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // ---------------- helpers ----------------

    private fun nowPlusMillis(deltaMs: Long): Instant =
        Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds() + deltaMs)

    private fun newTask(
        petId: String,
        date: Instant,
        id: String = UUID.randomUUID().toString(),
        seriesId: String? = null,
        status: taskStatusEnum = taskStatusEnum.planned,
        rrule: String? = null
    ): Task {
        return Task(
            id = id,
            seriesId = seriesId,
            petId = petId,
            type = taskTypeEnum.vet,
            title = "Test task $id",
            notes = null,
            status = status,
            priority = taskPriorityEnum.normal,
            createdAt = LocalDate(2024, 1, 1),
            date = date,
            rrule = rrule
        )
    }

    private fun isOccurrenceId(id: String): Boolean = id.contains("#")

    private fun occurrenceBaseId(occurrenceId: String): String = occurrenceId.substringBefore("#")

    private suspend fun deleteDocIfExists(path: String) {
        val ref = firestore.document(path)
        val snap = ref.get().await()
        if (snap.exists()) ref.delete().await()
    }

    // ---------------- tests ----------------

    @Test
    fun test_create_and_get_task_by_id_non_recurring() = runBlocking {
        val petId = "pet-${UUID.randomUUID()}"
        val date = nowPlusMillis(60 * 60 * 1000L) // +1h
        val task = newTask(petId = petId, date = date)

        taskRepo.createTask(task, rrule = null)

        val loaded = taskRepo.getTaskById(task.id)
        assertEquals(task.id, loaded.id)
        assertEquals(petId, loaded.petId)
        assertEquals(task.title, loaded.title)
        assertEquals(task.status, loaded.status)

        // cleanup
        deleteDocIfExists(FirestorePaths.taskDoc(task.id))
    }

    @Test
    fun test_get_tasks_by_pet_id_contains_created_task() = runBlocking {
        val petId = "pet-${UUID.randomUUID()}"
        val date = nowPlusMillis(2 * 60 * 60 * 1000L) // +2h
        val task = newTask(petId = petId, date = date)

        taskRepo.createTask(task, rrule = null)

        val tasks = taskRepo.getTasksByPetId(petId)
        assertTrue(tasks.any { it.id == task.id })

        // cleanup
        deleteDocIfExists(FirestorePaths.taskDoc(task.id))
    }

    @Test
    fun test_update_task_status_for_base_task() = runBlocking {
        val petId = "pet-${UUID.randomUUID()}"
        val date = nowPlusMillis(3 * 60 * 60 * 1000L)
        val task = newTask(petId = petId, date = date, status = taskStatusEnum.planned)

        taskRepo.createTask(task, rrule = null)

        taskRepo.updateTaskStatus(task.id, taskStatusEnum.done)

        val loaded = taskRepo.getTaskById(task.id)
        assertEquals(taskStatusEnum.done, loaded.status)

        // cleanup
        deleteDocIfExists(FirestorePaths.taskDoc(task.id))
    }

    @Test
    fun test_recurring_task_expands_occurrences_in_range() = runBlocking {
        val petId = "pet-${UUID.randomUUID()}"
        val baseId = UUID.randomUUID().toString()

        // start "soon", zeby nie wylecialo poza okno getTasksByPetId / date range
        val start = nowPlusMillis(30 * 60 * 1000L) // +30min
        val rrule = "FREQ=DAILY;COUNT=3"

        val base = newTask(
            petId = petId,
            date = start,
            id = baseId,
            // seriesId null - repo powinno je nadac przy createTask(rrule != null)
            seriesId = null,
            rrule = rrule
        )

        taskRepo.createTask(base, rrule)

        val from = Instant.fromEpochMilliseconds(start.toEpochMilliseconds() - 60_000L) // -1min
        val to = Instant.fromEpochMilliseconds(start.toEpochMilliseconds() + 5L * 24L * 60L * 60L * 1000L) // +5 days

        val tasks = taskRepo.getTasksByPetIdsInDateRange(listOf(petId), from, to)

        val occurrences = tasks.filter { isOccurrenceId(it.id) && occurrenceBaseId(it.id) == baseId }
        assertEquals(3, occurrences.size)

        // sanity: wszystko w zakresie
        assertTrue(occurrences.all { it.date >= from && it.date <= to })
        assertTrue(occurrences.all { it.petId == petId })
        assertTrue(occurrences.all { it.seriesId != null })

        // cleanup: usun baze serii (deleteWholeSeries = true) na bazowym dokumencie
        val baseLoaded = taskRepo.getTaskById(baseId)
        taskRepo.deleteTaskById(baseLoaded, deleteWholeSeries = true)

        // (opcjonalnie) twarda weryfikacja: baza zadania usunieta
        val snap = firestore.document(FirestorePaths.taskDoc(baseId)).get().await()
        assertFalse(snap.exists())
    }

    @Test
    fun test_update_status_for_single_occurrence_creates_override() = runBlocking {
        val petId = "pet-${UUID.randomUUID()}"
        val baseId = UUID.randomUUID().toString()
        val start = nowPlusMillis(15 * 60 * 1000L)
        val rrule = "FREQ=DAILY;COUNT=3"

        val base = newTask(petId = petId, date = start, id = baseId, rrule = rrule)
        taskRepo.createTask(base, rrule)

        val from = Instant.fromEpochMilliseconds(start.toEpochMilliseconds() - 60_000L)
        val to = Instant.fromEpochMilliseconds(start.toEpochMilliseconds() + 5L * 24L * 60L * 60L * 1000L)

        val tasks = taskRepo.getTasksByPetIdsInDateRange(listOf(petId), from, to)
        val occurrences = tasks.filter { isOccurrenceId(it.id) && occurrenceBaseId(it.id) == baseId }
            .sortedBy { it.date }

        assertEquals(3, occurrences.size)

        val target = occurrences[1] // srodkowe wystapienie
        taskRepo.updateTaskStatus(target.id, taskStatusEnum.done)

        val reloaded = taskRepo.getTaskById(target.id)
        assertEquals(taskStatusEnum.done, reloaded.status)

        // cleanup
        val baseLoaded = taskRepo.getTaskById(baseId)
        taskRepo.deleteTaskById(baseLoaded, deleteWholeSeries = true)
    }

    @Test
    fun test_delete_single_occurrence_hides_it_from_results() = runBlocking {
        val petId = "pet-${UUID.randomUUID()}"
        val baseId = UUID.randomUUID().toString()
        val start = nowPlusMillis(10 * 60 * 1000L)
        val rrule = "FREQ=DAILY;COUNT=3"

        val base = newTask(petId = petId, date = start, id = baseId, rrule = rrule)
        taskRepo.createTask(base, rrule)

        val from = Instant.fromEpochMilliseconds(start.toEpochMilliseconds() - 60_000L)
        val to = Instant.fromEpochMilliseconds(start.toEpochMilliseconds() + 5L * 24L * 60L * 60L * 1000L)

        val before = taskRepo.getTasksByPetIdsInDateRange(listOf(petId), from, to)
        val occBefore = before.filter { isOccurrenceId(it.id) && occurrenceBaseId(it.id) == baseId }
            .sortedBy { it.date }

        assertEquals(3, occBefore.size)

        val toDelete = occBefore[0]
        taskRepo.deleteTaskById(toDelete, deleteWholeSeries = false)

        val after = taskRepo.getTasksByPetIdsInDateRange(listOf(petId), from, to)
        val occAfter = after.filter { isOccurrenceId(it.id) && occurrenceBaseId(it.id) == baseId }

        assertEquals(2, occAfter.size)
        assertFalse(occAfter.any { it.id == toDelete.id })

        // cleanup
        val baseLoaded = taskRepo.getTaskById(baseId)
        taskRepo.deleteTaskById(baseLoaded, deleteWholeSeries = true)
    }
}
