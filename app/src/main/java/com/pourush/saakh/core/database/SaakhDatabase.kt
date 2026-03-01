package com.pourush.saakh.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WorkEntry::class, Contractor::class], // Added Contractor here!
    version = 2,
    exportSchema = false
)
abstract class SaakhDatabase : RoomDatabase() {
    abstract fun workDao(): WorkDao
    abstract fun contractorDao(): ContractorDao
}