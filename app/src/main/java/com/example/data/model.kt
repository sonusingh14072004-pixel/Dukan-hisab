package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Custom Shop details configured by the shopkeeper.
 * Shown on the app header and WhatsApp slips.
 */
@Entity(tableName = "shop_profile")
data class ShopProfile(
    @PrimaryKey val id: Int = 1,
    val shopName: String = "Mera Money Transfer & Online Services",
    val ownerName: String = "Dukandaar",
    val phone: String = "",
    val address: String = "Main Market",
    val upiId: String = ""
)

/**
 * Money Transfer Agent (e.g., Airtel Payments Bank, Spice Money, PayNearby, Fino, SBI Kiosk)
 */
@Entity(tableName = "agents")
data class Agent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val company: String = "",
    val phone: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType(val labelHindi: String, val labelEnglish: String) {
    GIVEN_TO_AGENT("एजेंट को दिए (Cash Diya)", "Cash Given to Agent"),
    RECEIVED_FROM_AGENT("एजेंट से लिए (Cash Liya)", "Cash Received from Agent")
}

/**
 * Daily Hisab Record / Settlement Entry
 */
@Entity(tableName = "cash_settlements")
data class CashSettlement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val agentId: Long,
    val agentName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // YYYY-MM-DD for fast date filtering
    val transactionType: String = TransactionType.GIVEN_TO_AGENT.name,
    // Note Counts
    val count500: Int = 0,
    val count200: Int = 0,
    val count100: Int = 0,
    val count50: Int = 0,
    val count20: Int = 0,
    val count10: Int = 0,
    val coinsAmount: Long = 0, // Direct coins total
    val totalNotes: Int = 0,
    val totalAmount: Long = 0,
    val commission: Long = 0,
    val remarks: String = ""
)

/**
 * UI State for the Cash Note Counter
 */
data class DenominationState(
    val count500: Int = 0,
    val count200: Int = 0,
    val count100: Int = 0,
    val count50: Int = 0,
    val count20: Int = 0,
    val count10: Int = 0,
    val coinsAmount: Long = 0
) {
    val total500: Long get() = count500.toLong() * 500
    val total200: Long get() = count200.toLong() * 200
    val total100: Long get() = count100.toLong() * 100
    val total50: Long get() = count50.toLong() * 50
    val total20: Long get() = count20.toLong() * 20
    val total10: Long get() = count10.toLong() * 10

    val totalNotes: Int
        get() = count500 + count200 + count100 + count50 + count20 + count10

    val grandTotal: Long
        get() = total500 + total200 + total100 + total50 + total20 + total10 + coinsAmount

    val isEmpty: Boolean
        get() = totalNotes == 0 && coinsAmount == 0L
}
