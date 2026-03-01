package com.pourush.saakh.core.utils
import com.pourush.saakh.core.database.WorkEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LedgerExporter {
    fun generateShareableText(entries: List<WorkEntry>): String {
        if (entries.isEmpty()) return "No work logged yet."

        val sb = StringBuilder()
        sb.append("🛠️ *Saakh Work Ledger* 🛠️\n")
        sb.append("------------------------\n\n")

        var totalVerifiedWage = 0.0
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        entries.forEach { entry ->
            val dateStr = dateFormat.format(Date(entry.date))
            val status = if (entry.isVerified) "✅ ${entry.contractorName}" else "⏳ Pending"

            sb.append("📅 *$dateStr*\n")
            sb.append("⏱️ ${entry.hoursWorked} hrs | 💰 ₹${entry.wageRate}\n")
            sb.append("Status: $status\n\n")

            if (entry.isVerified) {
                totalVerifiedWage += entry.wageRate
            }
        }

        sb.append("------------------------\n")
        sb.append("💰 *Total Verified Earnings: ₹$totalVerifiedWage*\n")
        sb.append("Generated via Saakh App")

        return sb.toString()
    }
}