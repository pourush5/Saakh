package com.pourush.saakh.core.database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkDao {

    //Get all entries, sorted by newest first
    @Query("SELECT * FROM work_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<WorkEntry>>
    //Using Flow so the UI updates automatically when data changes!

    @Query("SELECT * FROM work_entries WHERE id = :id")
    suspend fun getEntryById(id: String): WorkEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WorkEntry)

    @Update
    suspend fun updateEntry(entry: WorkEntry)

    @Delete
    suspend fun deleteEntry(entry: WorkEntry)

    //Quick stats query for the Dashboard
    @Query("SELECT SUM(wageRate) FROM work_entries")
    fun getTotalWagesExpected(): Flow<Double?>
}
