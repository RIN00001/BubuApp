package com.example.todolistapp.views.components.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolistapp.models.WalletModel
import com.example.todolistapp.utils.formatRupiah


@Composable
fun WalletCard(
    wallet: WalletModel,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (wallet.isDefault)
                Color(0xFFE1AFD1)
            else
                Color(0xFFFFE6E6)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = wallet.name,
                    style = MaterialTheme.typography.titleMedium
                )

                if (wallet.isDefault) {
                    Text(
                        text = "Default Wallet",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7469B6)
                    )
                }
            }

            Text(
                text = formatRupiah(wallet.balance),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}