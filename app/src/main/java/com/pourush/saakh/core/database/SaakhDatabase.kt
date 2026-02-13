package com.pourush.saakh.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WorkEntry::class], version = 1, exportSchema = false)
abstract class SaakhDatabase : RoomDatabase() {
    abstract fun workDao(): WorkDao
}