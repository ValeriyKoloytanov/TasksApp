package com.koloytanov.tasksApp.reminderRoom

import androidx.room.*
import com.koloytanov.tasksApp.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface Taskdao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Delete
    fun deleteTask(task: Task): Int

    @Query("SELECT * FROM task_table")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE id=:id")
    fun specificTask(id: Int): Task

    @Update
    fun updateTask(task: Task)
}