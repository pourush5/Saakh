package com.pourush.saakh.core.repository


import com.pourush.saakh.core.database.WorkDao
import com.pourush.saakh.core.database.WorkEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkRepository @Inject constructor(
    private val workDao: WorkDao
) {
    // Expose the stream of data from the DAO
    val allWorkEntries: Flow<List<WorkEntry>> = workDao.getAllEntries()

    suspend fun addWorkEntry(entry: WorkEntry) {
        workDao.insertEntry(entry)
    }

    suspend fun getWorkEntry(id: String): WorkEntry? {
        return workDao.getEntryById(id)
    }

    suspend fun deleteWorkEntry(entry: WorkEntry) {
        workDao.deleteEntry(entry)
    }
}