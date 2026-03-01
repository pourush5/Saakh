package com.pourush.saakh.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContractorDao {
    @Query("SELECT * FROM contractors WHERE publicKey = :pubKey LIMIT 1")
    suspend fun getContractorByKey(pubKey: String): Contractor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContractor(contractor: Contractor)
}