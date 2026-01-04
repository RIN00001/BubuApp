package com.example.todolistapp.views.components.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.models.BookModel
import com.example.todolistapp.models.WalletModel
import com.example.todolistapp.utils.formatRupiah

@Composable
fun BookSummaryCard(
    book: BookModel,
    hideValues: Boolean,
    onToggleVisibility: () -> Unit
) {
    // Calculate values from book data
    val income = book.totalIncome ?: 0.0
    val expense = book.totalExpense ?: 0.0
    val balance = book.wallets?.sumOf { it.balance } ?: 0.0
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Book icon and date selector with eye icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = book.name,
                    modifier = Modifier.size(40.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date navigation
                    IconButton(onClick = { /* TODO: Previous date */ }) {
                        Text("<", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "11/30/2025 - 12/30/2025",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(onClick = { /* TODO: Next date */ }) {
                        Text(">", fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { /* TODO: Open calendar */ }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Eye icon for hiding values
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (hideValues) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle visibility",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary values
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Income",
                    value = if (hideValues) "Rp****" else formatRupiah(income),
                    color = Color(0xFF4CAF50),
                    icon = Icons.AutoMirrored.Filled.TrendingUp
                )
                SummaryItem(
                    label = "Balance",
                    value = if (hideValues) "Rp****" else formatRupiah(balance),
                    color = Color(0xFF000000),
                    icon = Icons.Default.AccountBalance
                )
                SummaryItem(
                    label = "Expense",
                    value = if (hideValues) "Rp****" else formatRupiah(expense),
                    color = Color(0xFFF44336),
                    icon = Icons.AutoMirrored.Filled.TrendingDown
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookSummaryCardPreview() {
    val dummyWallets = listOf(
        WalletModel(id = 1, name = "Cash", balance = 500000.0),
        WalletModel(id = 2, name = "Bank", balance = 1500000.0)
    )

    val dummyBook = BookModel(
        id = 1,
        name = "Personal Budget",
        totalIncome = 3000000.0,
        totalExpense = 1200000.0,
        wallets = dummyWallets
    )

    BookSummaryCard(
        book = dummyBook,
        hideValues = false,
        onToggleVisibility = {}
    )
}

