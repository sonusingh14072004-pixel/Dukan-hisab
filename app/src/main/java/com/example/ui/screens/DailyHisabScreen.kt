package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CashSettlement
import com.example.data.TransactionType
import com.example.ui.MainViewModel
import com.example.ui.theme.WhatsAppDarkGreen
import com.example.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DailyHisabScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedDateString by viewModel.selectedDateString.collectAsState()
    val selectedAgentFilterId by viewModel.selectedAgentFilterId.collectAsState()
    val dailySettlements by viewModel.dailySettlements.collectAsState()
    val allAgents by viewModel.allAgents.collectAsState()

    var settlementToDelete by remember { mutableStateOf<CashSettlement?>(null) }

    val todayStr = CurrencyFormatter.getTodayDateString()
    val yesterdayStr = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    // Calculations for the day
    val totalGiven = dailySettlements
        .filter { it.transactionType == TransactionType.GIVEN_TO_AGENT.name }
        .sumOf { it.totalAmount }

    val totalReceived = dailySettlements
        .filter { it.transactionType == TransactionType.RECEIVED_FROM_AGENT.name }
        .sumOf { it.totalAmount }

    val netCashSettled = totalGiven - totalReceived

    // Date navigation helper
    fun shiftDate(days: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val date = sdf.parse(selectedDateString) ?: Date()
            val cal = Calendar.getInstance().apply { time = date }
            cal.add(Calendar.DAY_OF_YEAR, days)
            viewModel.setSelectedDate(sdf.format(cal.time))
        } catch (e: Exception) {
            viewModel.setSelectedDate(todayStr)
        }
    }

    // Android DatePickerDialog
    fun openDatePicker() {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val d = sdf.parse(selectedDateString)
            if (d != null) cal.time = d
        } catch (e: Exception) {}

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val chosenCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                viewModel.setSelectedDate(sdf.format(chosenCal.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("daily_hisab_screen")
    ) {
        // Date Selector Bar
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { shiftDate(-1) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { openDatePicker() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick Date",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = CurrencyFormatter.formatReadableDate(selectedDateString),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { shiftDate(1) },
                        enabled = selectedDateString < todayStr
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day")
                    }
                }

                // Quick buttons: Aaj / Kal / Cal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val isToday = selectedDateString == todayStr
                    FilterChip(
                        selected = isToday,
                        onClick = { viewModel.setSelectedDate(todayStr) },
                        label = { Text("आज (Today)") },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    val isYesterday = selectedDateString == yesterdayStr
                    FilterChip(
                        selected = isYesterday,
                        onClick = { viewModel.setSelectedDate(yesterdayStr) },
                        label = { Text("कल (Yesterday)") },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    FilterChip(
                        selected = !isToday && !isYesterday,
                        onClick = { openDatePicker() },
                        label = { Text("कैलेंडर") },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Daily Summary Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "दैनिक कुल हिसाब (Day Summary)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "${dailySettlements.size} लेन-देन",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Given
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "एजेंट को दिए",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFC62828)
                                )
                            }
                            Text(
                                text = CurrencyFormatter.formatRupees(totalGiven),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB71C1C),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Total Received
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "एजेंट से लिए",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            Text(
                                text = CurrencyFormatter.formatRupees(totalReceived),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Net Balance row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "नेट कैश हिसाब (Net Given):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyFormatter.formatRupees(netCashSettled),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Share Day Summary via WhatsApp
                    if (dailySettlements.isNotEmpty()) {
                        Button(
                            onClick = { viewModel.prepareShareDaySummary() },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("share_day_summary_btn")
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("पूरा दिन WhatsApp करें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Agent Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = selectedAgentFilterId == null,
                onClick = { viewModel.setAgentFilter(null) },
                label = { Text("सभी एजेंट्स (All)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            allAgents.forEach { agent ->
                val isSelected = selectedAgentFilterId == agent.id
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.setAgentFilter(if (isSelected) null else agent.id)
                    },
                    label = { Text(agent.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Settlements List
        if (dailySettlements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "इस तारीख का कोई हिसाब दर्ज नहीं है",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "नोट काउंटर से नया हिसाब जोड़ें",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dailySettlements, key = { it.id }) { item ->
                    SettlementItemCard(
                        settlement = item,
                        onShareWhatsApp = { viewModel.prepareShareForSettlement(item) },
                        onDelete = { settlementToDelete = item }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    settlementToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { settlementToDelete = null },
            title = { Text("हिसाब एंट्री डिलीट करें?") },
            text = {
                Text("क्या आप ${target.agentName} की ₹${target.totalAmount} वाली एंट्री हटाना चाहते हैं?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSettlement(target.id)
                        settlementToDelete = null
                        Toast.makeText(context, "एंट्री हटा दी गई", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("डिलीट करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { settlementToDelete = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
private fun SettlementItemCard(
    settlement: CashSettlement,
    onShareWhatsApp: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isGiven = settlement.transactionType == TransactionType.GIVEN_TO_AGENT.name

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("settlement_item_${settlement.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (isGiven) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isGiven) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isGiven) Color(0xFFC62828) else Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = settlement.agentName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${CurrencyFormatter.formatTime(settlement.timestamp)} • ${if (isGiven) "कैश दिए" else "कैश लिए"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyFormatter.formatRupees(settlement.totalAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isGiven) Color(0xFFC62828) else Color(0xFF2E7D32)
                    )
                    Text(
                        text = "${settlement.totalNotes} नोट",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expandable Note Breakdown
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "नोटों का विवरण:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Denomination Breakdown Badges
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (settlement.count500 > 0) BreakdownBadge("500", settlement.count500, settlement.count500 * 500)
                        if (settlement.count200 > 0) BreakdownBadge("200", settlement.count200, settlement.count200 * 200)
                        if (settlement.count100 > 0) BreakdownBadge("100", settlement.count100, settlement.count100 * 100)
                        if (settlement.count50 > 0) BreakdownBadge("50", settlement.count50, settlement.count50 * 50)
                        if (settlement.count20 > 0) BreakdownBadge("20", settlement.count20, settlement.count20 * 20)
                        if (settlement.count10 > 0) BreakdownBadge("10", settlement.count10, settlement.count10 * 10)
                        if (settlement.coinsAmount > 0) BreakdownBadge("सिक्के", 1, settlement.coinsAmount.toInt())
                    }

                    if (settlement.remarks.isNotBlank()) {
                        Text(
                            text = "💬 रिमार्क: ${settlement.remarks}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons: WhatsApp Slip & Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Button(
                            onClick = onShareWhatsApp,
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp पर्ची", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownBadge(denom: String, count: Int, total: Int) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = "₹$denom × $count = ₹$total",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
