package com.koloytanov.tasksApp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "task_table")


data class Task(
    @PrimaryKey

    var id: Int = 0,
    var date_start: Instant = Instant.now(),
    var date_finish: Instant = Instant.now(),
    var name: String = "",
    var description: String = "",
)