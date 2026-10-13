package com.example

import com.example.data.CashSettlement
import com.example.data.DenominationState
import com.example.data.ShopProfile
import com.example.data.TransactionType
import com.example.util.CurrencyFormatter
import com.example.util.WhatsAppHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testDenominationCalculation() {
        val state = DenominationState(
            count500 = 10,  // 5000
            count200 = 5,   // 1000
            count100 = 10,  // 1000
            count50 = 4,    // 200
            count20 = 5,    // 100
            count10 = 10,   // 100
            coinsAmount = 50 // 50
        )

        assertEquals(44, state.totalNotes)
        assertEquals(7450L, state.grandTotal)
    }

    @Test
    fun testCurrencyFormatting() {
        assertEquals("₹25,000", CurrencyFormatter.formatRupees(25000))
        assertEquals("₹1,50,000", CurrencyFormatter.formatRupees(150000))
    }

    @Test
    fun testWhatsAppReceiptFormatting() {
        val shop = ShopProfile(
            shopName = "Sharma Money Transfer",
            ownerName = "Ramesh Sharma",
            phone = "9876543210"
        )
        val settlement = CashSettlement(
            agentId = 1,
            agentName = "Airtel Payments Bank",
            timestamp = 1700000000000L,
            dateString = "2026-10-12",
            transactionType = TransactionType.GIVEN_TO_AGENT.name,
            count500 = 10,
            count200 = 5,
            totalNotes = 15,
            totalAmount = 6000L,
            remarks = "Evening settlement"
        )

        val receipt = WhatsAppHelper.buildSettlementReceipt(settlement, shop)
        assertTrue(receipt.contains("Sharma Money Transfer"))
        assertTrue(receipt.contains("Airtel Payments Bank"))
        assertTrue(receipt.contains("₹500 × 10 = ₹5000"))
        assertTrue(receipt.contains("₹200 × 5 = ₹1000"))
        assertTrue(receipt.contains("6,000"))
    }

    @Test
    fun testConvertToWords() {
        val wordsEng = CurrencyFormatter.convertToWordsEnglish(25500)
        assertTrue(wordsEng.contains("Twenty Five Thousand"))

        val wordsHindi = CurrencyFormatter.convertToWordsHindi(25500)
        assertTrue(wordsHindi.contains("हज़ार"))
    }
}
