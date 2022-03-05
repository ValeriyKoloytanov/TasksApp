@file:Suppress("unused", "unused")

package com.koloytanov.tasksApp.reminderRoom

import androidx.room.TypeConverter
import java.time.Instant


class Typeconverter {
    @TypeConverter
    fun toInstant(timestamp: Long?): Instant? {
        return if (timestamp == null) {
            null
        } else {
            Instant.ofEpochSecond(timestamp)
        }
    }

    @TypeConverter
    fun toLong(date: Instant?): Long? {
        return if (date == null) {
            null
        } else
            return date.epochSecond
    }
}