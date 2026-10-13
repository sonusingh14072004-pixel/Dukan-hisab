package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.CashSettlement
import com.example.data.ShopProfile
import com.example.data.TransactionType

object WhatsAppHelper {

    /**
     * Generates a professionally formatted text receipt for WhatsApp
     */
    fun buildSettlementReceipt(
        settlement: CashSettlement,
        shopProfile: ShopProfile?
    ): String {
        val shopName = shopProfile?.shopName?.ifBlank { "Mera Money Transfer" } ?: "Mera Money Transfer"
        val ownerName = shopProfile?.ownerName?.ifBlank { "" } ?: ""
        val shopPhone = shopProfile?.phone?.ifBlank { "" } ?: ""
        val formattedDate = CurrencyFormatter.formatReadableDateTime(settlement.timestamp)
        val formattedTotal = CurrencyFormatter.formatRupees(settlement.totalAmount)
        val wordsInHindi = CurrencyFormatter.convertToWordsHindi(settlement.totalAmount)

        val txTypeLabel = if (settlement.transactionType == TransactionType.GIVEN_TO_AGENT.name) {
            "🔴 एजेंट को कैश दिए (Cash Handover)"
        } else {
            "🟢 एजेंट से कैश प्राप्त (Cash Received)"
        }

        val sb = StringBuilder()
        sb.append("🧾 *मनी ट्रांसफर हिसाब पर्ची*\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🏪 *दुकान:* $shopName\n")
        if (ownerName.isNotBlank()) sb.append("👤 *दुकानदार:* $ownerName\n")
        if (shopPhone.isNotBlank()) sb.append("📞 *फ़ोन:* $shopPhone\n")
        sb.append("📅 *तारीख व समय:* $formattedDate\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🤝 *एजेंट नाम:* *${settlement.agentName}*\n")
        sb.append("📌 *लेन-देन:* $txTypeLabel\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("💵 *नोटों का विवरण (Cash Breakdown):*\n")

        var hasNotes = false
        if (settlement.count500 > 0) {
            sb.append("▪️ ₹500 × ${settlement.count500} = ₹${settlement.count500 * 500}\n")
            hasNotes = true
        }
        if (settlement.count200 > 0) {
            sb.append("▪️ ₹200 × ${settlement.count200} = ₹${settlement.count200 * 200}\n")
            hasNotes = true
        }
        if (settlement.count100 > 0) {
            sb.append("▪️ ₹100 × ${settlement.count100} = ₹${settlement.count100 * 100}\n")
            hasNotes = true
        }
        if (settlement.count50 > 0) {
            sb.append("▪️ ₹50 × ${settlement.count50} = ₹${settlement.count50 * 50}\n")
            hasNotes = true
        }
        if (settlement.count20 > 0) {
            sb.append("▪️ ₹20 × ${settlement.count20} = ₹${settlement.count20 * 20}\n")
            hasNotes = true
        }
        if (settlement.count10 > 0) {
            sb.append("▪️ ₹10 × ${settlement.count10} = ₹${settlement.count10 * 10}\n")
            hasNotes = true
        }
        if (settlement.coinsAmount > 0) {
            sb.append("▪️ सिक्के (Coins) = ₹${settlement.coinsAmount}\n")
            hasNotes = true
        }

        if (!hasNotes) {
            sb.append("▪️ कोई नोट विवरण दर्ज नहीं (Zero breakdown)\n")
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🔢 *कुल नोट:* ${settlement.totalNotes}\n")
        sb.append("💰 *कुल राशि (Total):* *$formattedTotal*\n")
        sb.append("📝 *शब्दों में:* $wordsInHindi\n")

        if (settlement.commission > 0) {
            sb.append("💳 *कमीशन / चार्ज:* ₹${settlement.commission}\n")
        }

        if (settlement.remarks.isNotBlank()) {
            sb.append("💬 *रिमार्क / नोट:* ${settlement.remarks}\n")
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🙏 *कृपया हिसाब मिलान कर पुष्टि करें।*")

        return sb.toString()
    }

    /**
     * Generates a Day-end summary report for WhatsApp
     */
    fun buildDailySummaryReport(
        dateString: String,
        settlements: List<CashSettlement>,
        shopProfile: ShopProfile?
    ): String {
        val shopName = shopProfile?.shopName?.ifBlank { "Mera Money Transfer" } ?: "Mera Money Transfer"
        val formattedDate = CurrencyFormatter.formatReadableDate(dateString)

        val totalGiven = settlements
            .filter { it.transactionType == TransactionType.GIVEN_TO_AGENT.name }
            .sumOf { it.totalAmount }

        val totalReceived = settlements
            .filter { it.transactionType == TransactionType.RECEIVED_FROM_AGENT.name }
            .sumOf { it.totalAmount }

        val netAmount = totalGiven - totalReceived

        val sb = StringBuilder()
        sb.append("📊 *दैनिक मनी ट्रांसफर हिसाब रिपोर्ट*\n")
        sb.append("🏪 *$shopName*\n")
        sb.append("📅 *तारीख:* $formattedDate\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🔴 *कुल एजेंट को दिए (Cash Out):* ${CurrencyFormatter.formatRupees(totalGiven)}\n")
        sb.append("🟢 *कुल एजेंट से प्राप्त (Cash In):* ${CurrencyFormatter.formatRupees(totalReceived)}\n")
        sb.append("⚖️ *नेट बैलेंस (Net Cash Given):* *${CurrencyFormatter.formatRupees(netAmount)}*\n")
        sb.append("🔢 *कुल लेन-देन संख्या:* ${settlements.size}\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📋 *एजेंट-वाइज़ विवरण:*\n")

        val groupedByAgent = settlements.groupBy { it.agentName }
        for ((agentName, list) in groupedByAgent) {
            val given = list.filter { it.transactionType == TransactionType.GIVEN_TO_AGENT.name }.sumOf { it.totalAmount }
            val received = list.filter { it.transactionType == TransactionType.RECEIVED_FROM_AGENT.name }.sumOf { it.totalAmount }
            sb.append("▪️ *$agentName*: दिए ${CurrencyFormatter.formatRupees(given)}")
            if (received > 0) sb.append(", लिए ${CurrencyFormatter.formatRupees(received)}")
            sb.append(" (${list.size} एंट्री)\n")
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("दुकान हिसाब ऐप द्वारा जनरेट किया गया।")
        return sb.toString()
    }

    /**
     * Shares receipt via WhatsApp. If targetPhoneNumber is provided, attempts direct chat.
     */
    fun shareViaWhatsApp(
        context: Context,
        message: String,
        targetPhoneNumber: String? = null
    ) {
        val cleanPhone = targetPhoneNumber?.filter { it.isDigit() } ?: ""

        if (cleanPhone.length >= 10) {
            // Direct WhatsApp message URL intent
            val phoneWithCountry = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
            val url = "https://api.whatsapp.com/send?phone=$phoneWithCountry&text=" + Uri.encode(message)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // Fallback to standard share
            }
        }

        // WhatsApp direct SEND intent
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            // WhatsApp business or regular not found / blocked, try WA Business
            val waBusinessIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp.w4b")
                putExtra(Intent.EXTRA_TEXT, message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(waBusinessIntent)
            } catch (ex: Exception) {
                // Fallback to universal Android Chooser
                val chooserIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val chooser = Intent.createChooser(chooserIntent, "Share Hisab via...")
                chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                try {
                    context.startActivity(chooser)
                } catch (err: Exception) {
                    Toast.makeText(context, "No sharing app found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Hisab Receipt") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "हिसाब कॉपी हो गया! (Copied to clipboard)", Toast.LENGTH_SHORT).show()
    }
}
