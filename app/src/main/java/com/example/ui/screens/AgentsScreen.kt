package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.ui.MainViewModel
import com.example.ui.theme.WhatsAppDarkGreen

@Composable
fun AgentsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allAgents by viewModel.allAgents.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var agentToEdit by remember { mutableStateOf<Agent?>(null) }
    var agentToDelete by remember { mutableStateOf<Agent?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_agent")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Agent")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("agents_screen")
        ) {
            // Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "मनी ट्रांसफर एजेंट सूची",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Airtel, Spice, PayNearby, CSP वॉलेट एजेंट्स",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (allAgents.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "कोई एजेंट नहीं मिला। नीचे '+' दबाकर जोड़ें।",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allAgents, key = { it.id }) { agent ->
                        AgentCard(
                            agent = agent,
                            onEdit = { agentToEdit = agent },
                            onDelete = { agentToDelete = agent },
                            onCall = {
                                if (agent.phone.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${agent.phone}"))
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "फ़ोन नंबर दर्ज नहीं है", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onWhatsApp = {
                                if (agent.phone.isNotBlank()) {
                                    val clean = agent.phone.filter { it.isDigit() }
                                    val phoneWithCountry = if (clean.length == 10) "91$clean" else clean
                                    val url = "https://api.whatsapp.com/send?phone=$phoneWithCountry"
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "WhatsApp नहीं खुल सका", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "फ़ोन नंबर दर्ज नहीं है", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Agent Dialog
    if (showAddDialog) {
        AgentFormDialog(
            title = "नया एजेंट जोड़ें",
            initialName = "",
            initialCompany = "",
            initialPhone = "",
            initialNotes = "",
            onConfirm = { name, company, phone, notes ->
                viewModel.addAgent(name, company, phone, notes)
                showAddDialog = false
                Toast.makeText(context, "एजेंट सुरक्षित हो गया!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showAddDialog = false }
        )
    }

    // Edit Agent Dialog
    agentToEdit?.let { target ->
        AgentFormDialog(
            title = "एजेंट संपादित करें",
            initialName = target.name,
            initialCompany = target.company,
            initialPhone = target.phone,
            initialNotes = target.notes,
            onConfirm = { name, company, phone, notes ->
                viewModel.updateAgent(
                    target.copy(
                        name = name,
                        company = company,
                        phone = phone,
                        notes = notes
                    )
                )
                agentToEdit = null
                Toast.makeText(context, "एजेंट अपडेट हो गया!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { agentToEdit = null }
        )
    }

    // Delete Agent Confirmation
    agentToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { agentToDelete = null },
            title = { Text("एजेंट हटाएं?") },
            text = { Text("क्या आप '${target.name}' को सूची से हटाना चाहते हैं?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAgent(target)
                        agentToDelete = null
                        Toast.makeText(context, "एजेंट हटा दिया गया", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("डिलीट करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { agentToDelete = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
private fun AgentCard(
    agent: Agent,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("agent_card_${agent.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = agent.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (agent.company.isNotBlank()) {
                        Text(
                            text = "🏢 ${agent.company}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (agent.phone.isNotBlank()) {
                        Text(
                            text = "📞 ${agent.phone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (agent.notes.isNotBlank()) {
                        Text(
                            text = "📝 ${agent.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (agent.phone.isNotBlank()) {
                        IconButton(onClick = onWhatsApp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "WhatsApp",
                                tint = WhatsAppDarkGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = onCall) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AgentFormDialog(
    title: String,
    initialName: String,
    initialCompany: String,
    initialPhone: String,
    initialNotes: String,
    onConfirm: (name: String, company: String, phone: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var company by remember { mutableStateOf(initialCompany) }
    var phone by remember { mutableStateOf(initialPhone) }
    var notes by remember { mutableStateOf(initialNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("एजेंट का नाम *") },
                    placeholder = { Text("उदा. रमेश कुमार / Fino CSP") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("form_agent_name")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("कंपनी / बैंक") },
                    placeholder = { Text("उदा. Airtel Bank, Spice, SBI") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("form_agent_company")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("WhatsApp / मोबाइल नंबर") },
                    placeholder = { Text("उदा. 9876543210") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("form_agent_phone")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("अतिरिक्त नोट (वैकल्पिक)") },
                    placeholder = { Text("उदा. रोज़ शाम 6 बजे सेटलमेंट") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("form_agent_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, company, phone, notes)
                    }
                }
            ) {
                Text("सुरक्षित करें (Save)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें")
            }
        }
    )
}
