package com.koloytanov.tasksApp.reminderRoom

import androidx.annotation.WorkerThread
import com.koloytanov.tasksApp.model.Task
import kotlinx.coroutines.flow.Flow

class Taskrepository(private val taskdao: Taskdao) {
    fun getNotes(): Flow<List<Task>> {
        return taskdao.getTasks()

    }

    fun getclickedtask(id: Int): Task {
        return taskdao.specificTask(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(task: Task) {
        taskdao.insert(task)
    }

    fun update(task: Task) {
        taskdao.updateTask(task)
    }

    fun delete(task: Task) {
        taskdao.deleteTask(task)
    }
}