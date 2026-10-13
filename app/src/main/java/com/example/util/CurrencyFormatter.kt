package com.example.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyFormatter {

    /**
     * Formats amount in Indian currency format with ₹ symbol (e.g. ₹ 1,50,000)
     */
    fun formatRupees(amount: Long): String {
        return try {
            val isNegative = amount < 0
            val absStr = kotlin.math.abs(amount).toString()
            val formatted = if (absStr.length <= 3) {
                absStr
            } else {
                val last3 = absStr.substring(absStr.length - 3)
                val remaining = absStr.substring(0, absStr.length - 3)
                val sb = StringBuilder()
                var count = 0
                for (i in remaining.length - 1 downTo 0) {
                    sb.append(remaining[i])
                    count++
                    if (count % 2 == 0 && i > 0) {
                        sb.append(',')
                    }
                }
                sb.reverse().toString() + "," + last3
            }
            if (isNegative) "-₹$formatted" else "₹$formatted"
        } catch (e: Exception) {
            "₹$amount"
        }
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun formatReadableDate(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(dateString) ?: return dateString
            val formatter = SimpleDateFormat("dd MMM yyyy (EEE)", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatReadableDateTime(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    /**
     * Converts numerical amount to words in English (Indian numbering system)
     */
    fun convertToWordsEnglish(num: Long): String {
        if (num == 0L) return "Zero Rupees Only"
        if (num < 0) return "Minus " + convertToWordsEnglish(-num)

        val units = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
        )
        val tens = arrayOf(
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        )

        fun numUnderThousand(n: Int): String {
            var str = ""
            if (n >= 100) {
                str += units[n / 100] + " Hundred "
            }
            val rem = n % 100
            if (rem in 1..19) {
                str += units[rem] + " "
            } else if (rem >= 20) {
                str += tens[rem / 10] + " "
                if (rem % 10 > 0) {
                    str += units[rem % 10] + " "
                }
            }
            return str.trim()
        }

        var remaining = num
        var result = ""

        // Crores
        val crores = remaining / 10000000L
        if (crores > 0) {
            result += numUnderThousand(crores.toInt()) + " Crore "
            remaining %= 10000000L
        }

        // Lakhs
        val lakhs = remaining / 100000L
        if (lakhs > 0) {
            result += numUnderThousand(lakhs.toInt()) + " Lakh "
            remaining %= 100000L
        }

        // Thousands
        val thousands = remaining / 1000L
        if (thousands > 0) {
            result += numUnderThousand(thousands.toInt()) + " Thousand "
            remaining %= 1000L
        }

        // Hundreds and tens
        if (remaining > 0) {
            result += numUnderThousand(remaining.toInt())
        }

        return result.trim() + " Rupees Only"
    }

    /**
     * Converts numerical amount to words in Hindi (देवनागरी)
     */
    fun convertToWordsHindi(num: Long): String {
        if (num == 0L) return "शून्य रुपये मात्र"
        if (num < 0) return "ऋण " + convertToWordsHindi(-num)

        val hindiUnits = mapOf(
            1 to "एक", 2 to "दो", 3 to "तीन", 4 to "चार", 5 to "पाँच",
            6 to "छह", 7 to "सात", 8 to "आठ", 9 to "नौ", 10 to "दस",
            11 to "ग्यारह", 12 to "बारह", 13 to "तेरह", 14 to "चौदह", 15 to "पंद्रह",
            16 to "सोलह", 17 to "सत्रह", 18 to "अठारह", 19 to "उन्नीस", 20 to "बीस",
            25 to "पच्चीस", 30 to "तीस", 40 to "चालीस", 50 to "पचास",
            60 to "साठ", 70 to "सत्तर", 80 to "अस्सी", 90 to "नब्बे"
        )

        // For simplicity and accuracy in common ledger amounts, provide natural Hindi phrasing
        var remaining = num
        val parts = mutableListOf<String>()

        val crores = remaining / 10000000L
        if (crores > 0) {
            parts.add("${hindiUnits[crores.toInt()] ?: crores} करोड़")
            remaining %= 10000000L
        }

        val lakhs = remaining / 100000L
        if (lakhs > 0) {
            parts.add("${hindiUnits[lakhs.toInt()] ?: lakhs} लाख")
            remaining %= 100000L
        }

        val thousands = remaining / 1000L
        if (thousands > 0) {
            parts.add("${hindiUnits[thousands.toInt()] ?: thousands} हज़ार")
            remaining %= 1000L
        }

        val hundreds = remaining / 100L
        if (hundreds > 0) {
            parts.add("${hindiUnits[hundreds.toInt()] ?: hundreds} सौ")
            remaining %= 100L
        }

        if (remaining > 0) {
            parts.add(hindiUnits[remaining.toInt()] ?: remaining.toString())
        }

        return parts.joinToString(" ") + " रुपये मात्र"
    }
}
