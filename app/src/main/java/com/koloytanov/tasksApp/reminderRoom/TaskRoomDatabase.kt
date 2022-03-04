package com.koloytanov.tasksApp.reminderRoom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.koloytanov.tasksApp.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Task::class], version = 3, exportSchema = false)
@TypeConverters(Typeconverter::class)

abstract class TaskRoomDatabase : RoomDatabase() {

    abstract fun taskdao(): Taskdao
    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.taskdao())
                }
            }
        }

        suspend fun populateDatabase(taskDao: Taskdao) {
            val mapper = jacksonObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            )
            mapper.registerModule(JavaTimeModule())
            val taskJson = """
[
   {
      "id":1,
      "date_start":147600000,
      "date_finish":147610000,
	"name": "My task",
	"description": "Описание моего дела"
   }
]"""

            val myList: List<Task> = mapper.readValue(taskJson)
            for (task in myList) {
                taskDao.insert(task)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TaskRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskRoomDatabase::class.java,
                    "tasks.db"
                ).addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}