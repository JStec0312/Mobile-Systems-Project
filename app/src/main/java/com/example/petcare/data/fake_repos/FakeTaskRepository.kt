package com.example.petcare.data.fake_repos

import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.config.DeveloperSettings
import com.example.petcare.data.dto.TaskDto
import com.example.petcare.data.mapper.toDomain
import com.example.petcare.data.mapper.toDto
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.repository.ITaskRepository
import com.example.petcare.exceptions.Failure
import com.example.petcare.exceptions.GeneralFailure
import kotlinx.datetime.Instant
import org.dmfs.rfc5545.DateTime
import org.dmfs.rfc5545.recur.RecurrenceRule
import java.util.TimeZone
import java.util.UUID

class FakeTaskRepository: ITaskRepository {
    private val tasks = mutableListOf<TaskDto>()
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
            date = ("2025-12-11T10:00:00Z")
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
        )
        tasks.add(task1)
        tasks.add(task2)
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
        if (rrule == null){
            tasks.add(task.toDto())
        }
        else{
            try{
                val rule = RecurrenceRule(rrule);
                val startMillis = task.date.toEpochMilliseconds()
                val startDateTime = DateTime(  TimeZone.getDefault(), startMillis);
                val iterator = rule.iterator(startDateTime);
                val maxDateTimeStamp = startMillis + (60L * 24 * 60 * 60 * 1000);
                var safetyCounter = 0;
                while (iterator.hasNext() && safetyCounter < 1000){
                    val nextInstance = iterator.next();
                    val nextTimeStamp = nextInstance.timestamp;
                    if (nextTimeStamp > maxDateTimeStamp){
                        break;
                    }
                    val nextInstant = Instant.fromEpochMilliseconds(nextTimeStamp);
                    val taskToSave = if (nextTimeStamp == startMillis) {
                        task
                    } else {
                        task.copy(
                            id = UUID.randomUUID().toString(),
                            date = nextInstant
                        )
                    }
                    tasks.add(taskToSave.toDto())
                    safetyCounter += 1
                }
            }
            catch (e: Exception){
                tasks.add(task.toDto())
                throw GeneralFailure.InvalidIntervalPassed()
            }
        }
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
        return tasks.filter { it.petId == petId }.map { it.toDomain() }
    }

    override fun updateTaskStatus(taskId: String, newStatus: taskStatusEnum){
        if (taskId == "Network Error") {
            throw Failure.NetworkError()
        }
        if (taskId == "Server Error") {
            throw Failure.ServerError()
        }
        if (taskId == "Unknown Error") {
            throw Failure.UnknownError()
        }
        val taskIndex = tasks.indexOfFirst { it.id == taskId}
        tasks[taskIndex].status = newStatus
        return
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
        return tasks.filter { petIds.contains(it.petId) }.map { it.toDomain() }
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
        if (deleteWholeSeries && task.seriesId != null){
            tasks.removeAll { it.seriesId == task.seriesId }
        }
        else{
            tasks.removeAll { it.id == task.id }
        }
    }
}
