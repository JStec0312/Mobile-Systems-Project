package com.example.petcare.data.fake_repos

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.config.DeveloperSettings
import com.example.petcare.data.dto.fake.TaskDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import timber.log.Timber
import java.util.TimeZone
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

private const val RECURRENCE_WINDOW_DAYS = 60L
private const val DAY_MS = 24L * 60 * 60 * 1000L
private data class TaskOccurrenceKey(
    val seriesId: String,
    val occurrenceAtMillis: Long
)
class FakeTaskRepository: ITaskRepository {
    private val tasks = mutableListOf<TaskDto>()
    private val recurringStatusOverrides = mutableMapOf<TaskOccurrenceKey, taskStatusEnum>()
    private val deletedOccurrences = mutableSetOf<TaskOccurrenceKey>()


    init{
        // Initialize with some fake tasks if needed
        val task1 = TaskDto(
            id = UUID.randomUUID().toString(),
            seriesId = null,
            petId = DeveloperSettings.PET_1_ID,
            type = taskTypeEnum.vet,
            title = "Vet Appointment",
            description = "Annual check-up",
            notes = "Bring vaccination records",
            status = taskStatusEnum.planned,
            priority = taskPriorityEnum.high,
            createdAt = DateConverter.localDateNow().toString(),
            date = ("2025-12-11T10:00:00")
        )
        val task2 = TaskDto(
            id = UUID.randomUUID().toString(),
            seriesId = null,
            petId = DeveloperSettings.PET_2_ID,
            type = taskTypeEnum.walk,
            title = "Morning Walk",
            description = "30-minute walk in the park",
            notes = null,
            status = taskStatusEnum.done,
            priority = taskPriorityEnum.normal,
            createdAt = DateConverter.localDateNow().toString(),
            date = ("2025-12-10T07:00:00Z")
        );
        val soonTask = TaskDto(
            id = UUID.randomUUID().toString(),
            seriesId = null,
            petId = DeveloperSettings.PET_1_ID,
            type = taskTypeEnum.feeding,
            title = "Feed Bella",
            description = "Evening meal",
            notes = "Use special diet food",
            status = taskStatusEnum.planned,
            priority = taskPriorityEnum.high,
            createdAt = DateConverter.localDateNow().toString(),
            date = (Clock.System.now() + 30.minutes).toString()
        );

        tasks.add(task1)
        tasks.add(task2)
        tasks.add(soonTask)
    }

    override fun createTask(task: Task, rrule: String?) {
        if (task.title == "Network Error") {
            throw Failure.NetworkError()
        }
        if (task.title == "Server Error") {
            throw Failure.ServerError()
        }
        if (task.title == "Unknown Error") {
            throw Failure.UnknownError()
        }

        if (rrule!=null){
            try{
                RecurrenceRule(rrule)
            } catch (e: Exception){
                throw GeneralFailure.InvalidIntervalPassed();
            }
        }
        tasks.add(task.toDto())

    }

    override fun getTasksByPetId(petId: String): List<Task> {
        if (petId == "Network Error") {
            throw Failure.NetworkError()
        }
        if (petId == "Server Error") {
            throw Failure.ServerError()
        }
        if (petId == "Unknown Error") {
            throw Failure.UnknownError()
        }

        return expandTasksForPetIds(listOf(petId))
    }
    override fun updateTaskStatus(taskId: String, newStatus: taskStatusEnum) {
        if (taskId == "Network Error") {
            throw Failure.NetworkError()
        }
        if (taskId == "Server Error") {
            throw Failure.ServerError()
        }
        if (taskId == "Unknown Error") {
            throw Failure.UnknownError()
        }

        val baseIndex = tasks.indexOfFirst { it.id == taskId }
        if (baseIndex >= 0) {
            val dto = tasks[baseIndex]
            tasks[baseIndex] = dto.copy(status = newStatus)
            return
        }
        val (baseId, ts) = parseOccurrenceId(taskId) ?: throw GeneralFailure.TaskNotFound()
        val baseTaskDto = tasks.find { it.id == baseId } ?: throw GeneralFailure.TaskNotFound()
        val baseTask = baseTaskDto.toDomain()
        val seriesId = baseTask.seriesId
            ?: throw GeneralFailure.TaskNotFound()
        val key = TaskOccurrenceKey(seriesId, ts)
        recurringStatusOverrides[key] = newStatus
    }

    override fun getTasksByPetIds(petIds: List<String>): List<Task> {
        if (petIds.contains("Network Error")) {
            throw Failure.NetworkError()
        }
        if (petIds.contains("Server Error")) {
            throw Failure.ServerError()
        }
        if (petIds.contains("Unknown Error")) {
            throw Failure.UnknownError()
        }

        return expandTasksForPetIds(petIds)
    }

    override fun getTaskById(taskId: String): Task {
        if (taskId == "Network Error") {
            throw Failure.NetworkError()
        }
        if (taskId == "Server Error") {
            throw Failure.ServerError()
        }
        if (taskId == "Unknown Error") {
            throw Failure.UnknownError()
        }
        val task = tasks.find { it.id == taskId }
        if (task == null) {
            throw GeneralFailure.TaskNotFound()
        }
        return task.toDomain()
    }

    override fun deleteTaskById(task: Task, deleteWholeSeries: Boolean) {
        if (task.id == "Network Error") {
            throw Failure.NetworkError()
        }
        if (task.id == "Server Error") {
            throw Failure.ServerError()
        }
        if (task.id == "Unknown Error") {
            throw Failure.UnknownError()
        }

        if (deleteWholeSeries && task.seriesId != null) {
            tasks.removeAll { it.seriesId == task.seriesId }
            recurringStatusOverrides.keys
                .filter { it.seriesId == task.seriesId }
                .forEach { recurringStatusOverrides.remove(it) }

            deletedOccurrences.removeIf { it.seriesId == task.seriesId }
            return
        }

        val removed = tasks.removeIf { it.id == task.id }
        if (removed) return
        val (baseId, ts) = parseOccurrenceId(task.id) ?: return
        val baseTaskDto = tasks.find { it.id == baseId } ?: return
        val seriesId = baseTaskDto.seriesId ?: return

        deletedOccurrences.add(TaskOccurrenceKey(seriesId, ts))
    }


    override fun updatateTask(
        task: Task,
        updateWholeSeries: Boolean
    ) {
        if (task.id == "Network Error") {
            throw Failure.NetworkError()
        }
        if (task.id == "Server Error") {
            throw Failure.ServerError()
        }
        if (task.id == "Unknown Error") {
            throw Failure.UnknownError()
        }
        if (updateWholeSeries && task.seriesId != null){
            val tasksToUpdate = tasks.filter { it.seriesId == task.seriesId }
            if (tasksToUpdate.isEmpty()){
                throw GeneralFailure.TaskNotFound()
            }
            for (t in tasksToUpdate){
                val index = tasks.indexOfFirst { it.id == t.id }
                tasks[index] = task.toDto()
            }
        }
        else{
            val index = tasks.indexOfFirst { it.id == task.id }
            if (index == -1){
                throw GeneralFailure.TaskNotFound()
            }
            tasks[index] = task.toDto()
        }
    }

    override fun getTasksByPetIdsInDateRange(
        petIds: List<String>,
        from: Instant,
        to: Instant
    ): List<Task> {
        if (petIds.contains("Network Error")) {
            throw Failure.NetworkError()
        }
        if (petIds.contains("Server Error")) {
            throw Failure.ServerError()
        }
        if (petIds.contains("Unknown Error")) {
            throw Failure.UnknownError()
        }

        require(from <= to) { "from must be <= to" }

        return expandTasksForPetIdsInRange(petIds, from, to)
    }
    private fun buildOccurrenceId(baseId: String, ts: Long): String =
        "$baseId#$ts"

    private fun parseOccurrenceId(taskId: String): Pair<String, Long>? {
        val parts = taskId.split("#")
        if (parts.size != 2) return null
        val baseId = parts[0]
        val ts = parts[1].toLongOrNull() ?: return null
        return baseId to ts
    }

    private fun statusForOccurrence(
        baseTask: Task,
        ts: Long
    ): taskStatusEnum {
        val seriesId = baseTask.seriesId ?: return baseTask.status
        val key = TaskOccurrenceKey(seriesId, ts)
        return recurringStatusOverrides[key] ?: baseTask.status
    }

    private fun isOccurrenceDeleted(
        baseTask: Task,
        ts: Long
    ): Boolean {
        val seriesId = baseTask.seriesId ?: return false
        val key = TaskOccurrenceKey(seriesId, ts)
        return deletedOccurrences.contains(key)
    }
    private fun expandTasksForPetIds(
        petIds: List<String>
    ): List<Task> {
        val nowMs = Clock.System.now().toEpochMilliseconds() - DAY_MS
        val fromMs = nowMs
        val toMs = nowMs + RECURRENCE_WINDOW_DAYS * DAY_MS

        val from = Instant.fromEpochMilliseconds(fromMs)
        val to = Instant.fromEpochMilliseconds(toMs)

        val baseTasks = tasks
            .filter { petIds.contains(it.petId) }
            .map { it.toDomain() }

        val result = mutableListOf<Task>()

        for (task in baseTasks) {
            if (task.rrule == null) {
                if (task.date in from..to) {
                    result.add(task)
                }
            } else {
                try {
                    val rule = RecurrenceRule(task.rrule)
                    val startMillis = task.date.toEpochMilliseconds()
                    val startDateTime = DateTime(TimeZone.getDefault(), startMillis)
                    val iterator = rule.iterator(startDateTime)

                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        val ts = next.timestamp

                        if (ts > toMs) break
                        if (ts < fromMs) continue

                        if (isOccurrenceDeleted(task, ts)) {
                            continue
                        }

                        val instant = Instant.fromEpochMilliseconds(ts)
                        val status = statusForOccurrence(task, ts)

                        val occurrenceTask = task.copy(
                            id = buildOccurrenceId(task.id, ts),
                            date = instant,
                            status = status
                        )

                        result.add(occurrenceTask)
                    }
                } catch (e: Exception) {
                    if (task.date in from..to) {
                        result.add(task)
                    }
                }
            }
        }

        return result
    }

    private fun expandTasksForPetIdsInRange(
        petIds: List<String>,
        from: Instant,
        to: Instant
    ): List<Task> {
        Timber.d("Expanding tasks for pets $petIds in range $from to $to")

        val fromMs = from.toEpochMilliseconds()
        val toMs = to.toEpochMilliseconds()

        val baseTasks = tasks
            .filter { petIds.contains(it.petId) }
            .map { it.toDomain() }

        val result = mutableListOf<Task>()

        for (task in baseTasks) {
            if (task.rrule == null) {
                // zwykły task – po prostu filtr po dacie
                if (task.date >= from && task.date <= to) {
                    result.add(task)
                }
            } else {
                try {
                    val rule = RecurrenceRule(task.rrule)
                    val startMillis = task.date.toEpochMilliseconds()
                    val startDateTime = DateTime(TimeZone.getDefault(), startMillis)
                    val iterator = rule.iterator(startDateTime)

                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        val ts = next.timestamp

                        if (ts > toMs) break
                        if (ts < fromMs) continue

                        if (isOccurrenceDeleted(task, ts)) {
                            continue
                        }

                        val instant = Instant.fromEpochMilliseconds(ts)
                        val status = statusForOccurrence(task, ts)

                        val occurrenceTask = task.copy(
                            id = buildOccurrenceId(task.id, ts),
                            date = instant,
                            status = status
                        )

                        result.add(occurrenceTask)
                    }
                } catch (e: Exception) {
                    if (task.date >= from && task.date <= to) {
                        result.add(task)
                    }
                }
            }
        }

        return result
    }

}
