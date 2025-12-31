package com.example.todolistapp.views.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.models.ItemModel
import com.example.todolistapp.utils.DateFormatter // Pastikan kamu sudah buat ini di langkah sebelumnya
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ItemCard(
    item: ItemModel,
    modifier: Modifier = Modifier
) {
    // Format Rupiah
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val priceString = formatter.format(item.amount)

    // Warna background berdasarkan tipe (Income/Expense)
    val cardColor = if (item.type == "INCOME") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val textColor = if (item.type == "INCOME") Color(0xFF2E7D32) else Color(0xFFC62828)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = priceString,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = DateFormatter.format(item.date),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}