package com.pourush.saakh.core.repository


import com.pourush.saakh.core.database.Contractor
import com.pourush.saakh.core.database.ContractorDao
import com.pourush.saakh.core.database.WorkDao
import com.pourush.saakh.core.database.WorkEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkRepository @Inject constructor(
    private val workDao: WorkDao,
    private val contractorDao: ContractorDao
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
    suspend fun getContractor(pubKey: String) = contractorDao.getContractorByKey(pubKey)
    suspend fun addContractor(contractor: Contractor) = contractorDao.insertContractor(contractor)

}