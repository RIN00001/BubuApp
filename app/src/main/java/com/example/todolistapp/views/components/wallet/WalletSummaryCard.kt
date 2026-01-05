package com.example.todolistapp.views.components.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.models.WalletSummary
import com.example.todolistapp.utils.formatRupiah

@Composable
fun WalletSummaryCard(
    summary: WalletSummary,
    currentDateLabel: String,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Wallet Name
            Text(
                text = summary.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1B1F)
            )

            // Balance
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Current Balance",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatRupiah(summary.balance),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7469B6)
                )
            }

            // Default Badge
            if (summary.isDefault) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Default Wallet",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Divider(color = Color(0xFFE0E0E0))

            // Date Filter (Clickable)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onDateClick),
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Period:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = currentDateLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7469B6)
                    )
                }
            }

            // Income/Expense Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Income
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Income",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = formatRupiah(summary.totalIncome),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }

                // Expense
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Expense",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = formatRupiah(summary.totalExpense),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF44336)
                    )
                }
            }

            // Net Flow
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Net Flow",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatRupiah(summary.netFlow),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (summary.netFlow >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }
    }
}

