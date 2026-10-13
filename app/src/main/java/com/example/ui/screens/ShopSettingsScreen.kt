package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ShopProfile
import com.example.ui.MainViewModel
import com.example.ui.theme.WhatsAppChatBg

@Composable
fun ShopSettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentProfile by viewModel.shopProfile.collectAsState()

    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }

    LaunchedEffect(currentProfile) {
        currentProfile?.let {
            shopName = it.shopName
            ownerName = it.ownerName
            phone = it.phone
            address = it.address
            upiId = it.upiId
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("shop_settings_screen")
    ) {
        // Hero Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "दुकान प्रोफाइल व सेटिंग",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "यहाँ अपना दुकान नाम सेट करें जो पर्ची पर दिखेगा",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Fields Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "दुकान की जानकारी (Shop Details):",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("दुकान का नाम (Shop Name) *") },
                    placeholder = { Text("उदा. शर्मा मनी ट्रांसफर एवं ऑनलाइन सेंटर") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Store, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_shop_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("दुकानदार / संचालक का नाम") },
                    placeholder = { Text("उदा. रमेश शर्मा") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_owner_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("दुकान का WhatsApp / मोबाइल नंबर") },
                    placeholder = { Text("उदा. 9876543210") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_shop_phone")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("दुकान का पता / शहर (Address)") },
                    placeholder = { Text("उदा. मेन मार्केट, नई दिल्ली") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_shop_address")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI आईडी / QR कोड (वैकल्पिक)") },
                    placeholder = { Text("उदा. shop@upi") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_shop_upi")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (shopName.isBlank()) {
                            Toast.makeText(context, "कृपया दुकान का नाम दर्ज करें", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newProfile = ShopProfile(
                            id = 1,
                            shopName = shopName.trim(),
                            ownerName = ownerName.trim(),
                            phone = phone.trim(),
                            address = address.trim(),
                            upiId = upiId.trim()
                        )
                        viewModel.updateShopProfile(newProfile)
                        Toast.makeText(context, "दुकान की जानकारी अपडेट हो गई!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_shop_settings_button")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("जानकारी सुरक्षित करें (Save Profile)", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WhatsApp Slip Live Header Preview
        Text(
            text = "पर्ची पर ऐसा दिखेगा (WhatsApp Slip Header Preview):",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = WhatsAppChatBg),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFD4C8B8), RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🧾 *मनी ट्रांसफर हिसाब पर्ची*",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E2824)
                )
                Text(
                    text = "━━━━━━━━━━━━━━━━━━━━",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color(0xFF78909C)
                )
                Text(
                    text = "🏪 *दुकान:* ${shopName.ifBlank { "Mera Money Transfer" }}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2824)
                )
                if (ownerName.isNotBlank()) {
                    Text(
                        text = "👤 *दुकानदार:* $ownerName",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = Color(0xFF1E2824)
                    )
                }
                if (phone.isNotBlank()) {
                    Text(
                        text = "📞 *फ़ोन:* $phone",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = Color(0xFF1E2824)
                    )
                }
                Text(
                    text = "📅 *तारीख व समय:* 12 Oct 2026, 11:30 AM",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = Color(0xFF1E2824)
                )
                Text(
                    text = "━━━━━━━━━━━━━━━━━━━━",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color(0xFF78909C)
                )
                Text(
                    text = "💰 *कुल राशि (Total):* ₹ 25,000",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1B5E20)
                )
            }
        }
    }
}
