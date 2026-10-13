package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.CurrencyFormatter

@Composable
fun NoteRow(
    denominationValue: Int,
    denominationLabel: String,
    count: Int,
    badgeColor: Color,
    badgeBgColor: Color,
    onCountChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val subtotal = count.toLong() * denominationValue

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (count > 0) badgeBgColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (count > 0) 2.dp else 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = if (count > 0) 1.5.dp else 1.dp,
                color = if (count > 0) badgeColor.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("note_row_$denominationValue")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Denomination Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor,
                    modifier = Modifier.width(76.dp)
                ) {
                    Text(
                        text = denominationLabel,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                    )
                }

                Text(
                    text = "×",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                // Note Count input & decrement/increment
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { if (count > 0) onCountChanged(count - 1) },
                        enabled = count > 0,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (count > 0) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .testTag("decrement_$denominationValue")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease count",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    OutlinedTextField(
                        value = if (count == 0) "" else count.toString(),
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            val newCount = clean.toIntOrNull() ?: 0
                            onCountChanged(newCount)
                        },
                        placeholder = {
                            Text(
                                "0",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = badgeColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .width(72.dp)
                            .height(48.dp)
                            .padding(horizontal = 4.dp)
                            .testTag("input_$denominationValue")
                    )

                    IconButton(
                        onClick = { onCountChanged(count + 1) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(badgeColor.copy(alpha = 0.15f))
                            .testTag("increment_$denominationValue")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase count",
                            tint = badgeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Subtotal Display
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = CurrencyFormatter.formatRupees(subtotal),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (count > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (count > 0) {
                        Text(
                            text = "$count नोट",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick increment chips (+1, +5, +10, +50, Clear)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (count > 0) {
                    QuickChip(label = "Clear", color = MaterialTheme.colorScheme.error) {
                        onCountChanged(0)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                QuickChip(label = "+1", color = badgeColor) { onCountChanged(count + 1) }
                Spacer(modifier = Modifier.width(6.dp))
                QuickChip(label = "+5", color = badgeColor) { onCountChanged(count + 5) }
                Spacer(modifier = Modifier.width(6.dp))
                QuickChip(label = "+10", color = badgeColor) { onCountChanged(count + 10) }
                Spacer(modifier = Modifier.width(6.dp))
                QuickChip(label = "+50", color = badgeColor) { onCountChanged(count + 50) }
            }
        }
    }
}

@Composable
fun CoinsRow(
    coinsAmount: Long,
    badgeColor: Color,
    badgeBgColor: Color,
    onCoinsChanged: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (coinsAmount > 0) badgeBgColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (coinsAmount > 0) 2.dp else 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = if (coinsAmount > 0) 1.5.dp else 1.dp,
                color = if (coinsAmount > 0) badgeColor.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("coins_row")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor,
                    modifier = Modifier.width(76.dp)
                ) {
                    Text(
                        text = "सिक्के 🪙",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                    )
                }

                Text(
                    text = "₹",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                OutlinedTextField(
                    value = if (coinsAmount == 0L) "" else coinsAmount.toString(),
                    onValueChange = { input ->
                        val clean = input.filter { it.isDigit() }
                        onCoinsChanged(clean.toLongOrNull() ?: 0L)
                    },
                    placeholder = {
                        Text("सिक्कों की कुल राशि", fontSize = 13.sp)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .padding(horizontal = 8.dp)
                        .testTag("input_coins")
                )

                Text(
                    text = CurrencyFormatter.formatRupees(coinsAmount),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = if (coinsAmount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick add coin amounts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (coinsAmount > 0) {
                    QuickChip(label = "Clear", color = MaterialTheme.colorScheme.error) {
                        onCoinsChanged(0L)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                QuickChip(label = "+₹10", color = badgeColor) { onCoinsChanged(coinsAmount + 10) }
                Spacer(modifier = Modifier.width(6.dp))
                QuickChip(label = "+₹20", color = badgeColor) { onCoinsChanged(coinsAmount + 20) }
                Spacer(modifier = Modifier.width(6.dp))
                QuickChip(label = "+₹50", color = badgeColor) { onCoinsChanged(coinsAmount + 50) }
                Spacer(modifier = Modifier.width(6.dp))
                QuickChip(label = "+₹100", color = badgeColor) { onCoinsChanged(coinsAmount + 100) }
            }
        }
    }
}

@Composable
private fun QuickChip(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
