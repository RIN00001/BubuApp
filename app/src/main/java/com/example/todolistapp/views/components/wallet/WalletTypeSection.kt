package com.example.todolistapp.views.components.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.utils.formatRupiah

@Composable
fun WalletTypeSection(
    type: String,
    totalAssets: Double,
    hideValues: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFE6E6),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = type,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9B8FC7)
            )

            Text(
                text = if (hideValues) "Assets: Rp****" else "Assets: ${formatRupiah(totalAssets)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

