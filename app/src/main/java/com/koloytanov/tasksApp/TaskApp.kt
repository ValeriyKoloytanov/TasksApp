package com.koloytanov.tasksApp

import android.app.Application
import com.koloytanov.tasksApp.reminderRoom.TaskRoomDatabase
import com.koloytanov.tasksApp.reminderRoom.Taskrepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TaskApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { TaskRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { Taskrepository(database.taskdao()) }
}
