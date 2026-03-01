package com.pourush.saakh.core.database
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "work_entries")
data class WorkEntry(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(), //Unique ID for every entry

    //The Labor Data (What the user types)
    val date: Long,             // Epoch timestamp (e.g., 1707833000000)
    val hoursWorked: Float,
    val wageRate: Double,       // (Daily rate)
    val notes: String = "",     // Optional: "Site A", "Plumbing", etc.

    //The Payment Data
    val amountPaid: Double = 0.0, //Part payment received
    val isFullyPaid: Boolean = false,

    // The Crypto Proof (The "Handshake")
    val isVerified: Boolean = false, //Turns GREEN if true
    val contractorPublicKey: String? = null, //Who verified it?
    val contractorName: String? = null, //Name of contractor
    val digitalSignature: String? = null, //The math proof
    val verifiedAt: Long? = null //When was it verified?
)