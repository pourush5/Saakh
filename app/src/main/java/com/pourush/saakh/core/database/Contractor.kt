package com.pourush.saakh.core.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contractors")
data class Contractor(
    @PrimaryKey
    val publicKey: String, // The cryptographic key IS their unique ID
    val name: String,      // The human-readable name (e.g. "Ramesh Thekedar")
    val addedAt: Long = System.currentTimeMillis()
)