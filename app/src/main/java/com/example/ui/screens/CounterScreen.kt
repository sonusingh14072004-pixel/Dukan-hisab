package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Agent
import com.example.data.TransactionType
import com.example.ui.MainViewModel
import com.example.ui.components.CoinsRow
import com.example.ui.components.NoteRow
import com.example.ui.theme.CoinBg
import com.example.ui.theme.CoinColor
import com.example.ui.theme.Note100Bg
import com.example.ui.theme.Note100Color
import com.example.ui.theme.Note10Bg
import com.example.ui.theme.Note10Color
import com.example.ui.theme.Note200Bg
import com.example.ui.theme.Note200Color
import com.example.ui.theme.Note20Bg
import com.example.ui.theme.Note20Color
import com.example.ui.theme.Note500Bg
import com.example.ui.theme.Note500Color
import com.example.ui.theme.Note50Bg
import com.example.ui.theme.Note50Color
import com.example.ui.theme.WhatsAppDarkGreen
import com.example.util.CurrencyFormatter

@Composable
fun CounterScreen(
    viewModel: MainViewModel,
    onNavigateToShopSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shopProfile by viewModel.shopProfile.collectAsState()
    val allAgents by viewModel.allAgents.collectAsState()
    val denominationState by viewModel.denominationState.collectAsState()
    val selectedAgent by viewModel.selectedAgent.collectAsState()
    val transactionType by viewModel.transactionType.collectAsState()
    val commission by viewModel.commission.collectAsState()
    val remarks by viewModel.remarks.collectAsState()

    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showAddAgentDialog by remember { mutableStateOf(false) }
    var showExtraFields by remember { mutableStateOf(false) }

    val shopName = shopProfile?.shopName?.ifBlank { "Mera Money Transfer" } ?: "Mera Money Transfer"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Shop Header Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToShopSettings() }
                    .testTag("shop_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = shopName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1
                            )
                            val owner = shopProfile?.ownerName?.ifBlank { "दुकानदार" } ?: "दुकानदार"
                            Text(
                                text = "👤 $owner • मनी ट्रांसफर खाता",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Shop",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transaction Type Selector (Cash Diya vs Cash Liya)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isGiven = transactionType == TransactionType.GIVEN_TO_AGENT
                Button(
                    onClick = { viewModel.setTransactionType(TransactionType.GIVEN_TO_AGENT) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isGiven) Color(0xFFC62828) else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isGiven) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_cash_diya")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "एजेंट को दिए",
                        fontWeight = if (isGiven) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }

                val isReceived = transactionType == TransactionType.RECEIVED_FROM_AGENT
                Button(
                    onClick = { viewModel.setTransactionType(TransactionType.RECEIVED_FROM_AGENT) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isReceived) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isReceived) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_cash_liya")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "एजेंट से लिए",
                        fontWeight = if (isReceived) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Agent Selection Chips
            Text(
                text = "मनी ट्रांसफर एजेंट चुनें:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick + Add Agent chip
                FilterChip(
                    selected = false,
                    onClick = { showAddAgentDialog = true },
                    label = { Text("+ नया एजेंट") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.testTag("add_agent_chip")
                )

                allAgents.forEach { agent ->
                    val isSelected = selectedAgent?.id == agent.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectAgent(if (isSelected) null else agent) },
                        label = { Text(agent.name) },
                        leadingIcon = if (isSelected) {
                            { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Denomination Note Rows
            NoteRow(
                denominationValue = 500,
                denominationLabel = "₹500",
                count = denominationState.count500,
                badgeColor = Note500Color,
                badgeBgColor = Note500Bg,
                onCountChanged = { viewModel.updateNoteCount(500, it) }
            )

            NoteRow(
                denominationValue = 200,
                denominationLabel = "₹200",
                count = denominationState.count200,
                badgeColor = Note200Color,
                badgeBgColor = Note200Bg,
                onCountChanged = { viewModel.updateNoteCount(200, it) }
            )

            NoteRow(
                denominationValue = 100,
                denominationLabel = "₹100",
                count = denominationState.count100,
                badgeColor = Note100Color,
                badgeBgColor = Note100Bg,
                onCountChanged = { viewModel.updateNoteCount(100, it) }
            )

            NoteRow(
                denominationValue = 50,
                denominationLabel = "₹50",
                count = denominationState.count50,
                badgeColor = Note50Color,
                badgeBgColor = Note50Bg,
                onCountChanged = { viewModel.updateNoteCount(50, it) }
            )

            NoteRow(
                denominationValue = 20,
                denominationLabel = "₹20",
                count = denominationState.count20,
                badgeColor = Note20Color,
                badgeBgColor = Note20Bg,
                onCountChanged = { viewModel.updateNoteCount(20, it) }
            )

            NoteRow(
                denominationValue = 10,
                denominationLabel = "₹10",
                count = denominationState.count10,
                badgeColor = Note10Color,
                badgeBgColor = Note10Bg,
                onCountChanged = { viewModel.updateNoteCount(10, it) }
            )

            CoinsRow(
                coinsAmount = denominationState.coinsAmount,
                badgeColor = CoinColor,
                badgeBgColor = CoinBg,
                onCoinsChanged = { viewModel.updateCoins(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Optional Commission & Remarks toggle
            TextButton(
                onClick = { showExtraFields = !showExtraFields },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (showExtraFields) "▲ रिमार्क व फीस छुपाएं" else "▼ रिमार्क / कमीशन जोड़ें (+)")
            }

            if (showExtraFields) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { viewModel.setRemarks(it) },
                            label = { Text("रिमार्क / नोट (Remarks)") },
                            placeholder = { Text("उदा. शाम का कैश, वॉलेट लोड") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_remarks")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = if (commission == 0L) "" else commission.toString(),
                            onValueChange = {
                                val clean = it.filter { c -> c.isDigit() }
                                viewModel.setCommission(clean.toLongOrNull() ?: 0L)
                            },
                            label = { Text("कमीशन / फीस (₹)") },
                            placeholder = { Text("0") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_commission")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Sticky Bottom Summary Bar
        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("counter_bottom_bar")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Top row: Total Notes & Grand Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "कुल राशि: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "${denominationState.totalNotes} नोट",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Grand total amount in rupees
                        Text(
                            text = CurrencyFormatter.formatRupees(denominationState.grandTotal),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("grand_total_text")
                        )
                    }

                    // Reset / Clear counter icon button
                    if (!denominationState.isEmpty) {
                        IconButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                                .testTag("clear_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = "Clear Counter",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Amount in words
                if (denominationState.grandTotal > 0) {
                    Text(
                        text = CurrencyFormatter.convertToWordsHindi(denominationState.grandTotal),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Action buttons: Save & WhatsApp Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Save Button
                    OutlinedButton(
                        onClick = {
                            if (denominationState.isEmpty) {
                                Toast.makeText(context, "कृपया पहले नोटों की गिनती दर्ज करें", Toast.LENGTH_SHORT).show()
                                return@OutlinedButton
                            }
                            viewModel.saveCurrentSettlement {
                                Toast.makeText(context, "हिसाब सुरक्षित हो गया! (Saved)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !denominationState.isEmpty,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_settlement_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("सेव करें", fontWeight = FontWeight.Bold)
                    }

                    // WhatsApp Direct Share Button
                    Button(
                        onClick = {
                            if (denominationState.isEmpty) {
                                Toast.makeText(context, "कृपया पहले नोटों की गिनती दर्ज करें", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.saveAndOpenWhatsAppShare {
                                Toast.makeText(context, "हिसाब सेव हो गया! WhatsApp पर्ची तैयार है", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !denominationState.isEmpty,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkGreen),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("whatsapp_share_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WhatsApp भेजें",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Confirm Clear Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("गिनती रीसेट करें?") },
            text = { Text("क्या आप दर्ज किए गए सभी नोटों की संख्या साफ़ करना चाहते हैं?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCounter()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("हाँ, रीसेट करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }

    // Add Agent Quick Dialog
    if (showAddAgentDialog) {
        var agentNameInput by remember { mutableStateOf("") }
        var companyInput by remember { mutableStateOf("") }
        var phoneInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddAgentDialog = false },
            title = { Text("नया एजेंट जोड़ें") },
            text = {
                Column {
                    OutlinedTextField(
                        value = agentNameInput,
                        onValueChange = { agentNameInput = it },
                        label = { Text("एजेंट का नाम *") },
                        placeholder = { Text("उदा. रमेश कुमार / Fino CSP") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = companyInput,
                        onValueChange = { companyInput = it },
                        label = { Text("कंपनी / बैंक") },
                        placeholder = { Text("उदा. Airtel, Spice Money, SBI") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("WhatsApp / फ़ोन नंबर") },
                        placeholder = { Text("उदा. 9876543210") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (agentNameInput.isNotBlank()) {
                            viewModel.addAgent(agentNameInput, companyInput, phoneInput, "")
                            showAddAgentDialog = false
                            Toast.makeText(context, "एजेंट जुड़ गया!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("जोड़ें (Save)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAgentDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}
