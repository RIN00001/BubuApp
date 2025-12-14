package com.example.todolistapp.views.components.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolistapp.models.WalletModel

import com.example.todolistapp.utils.formatRupiah

@Composable
fun WalletInfo(
    wallets: List<WalletModel>
) {
    val assets = wallets.sumOf { it.balance }
    val debt = 0.0
    val netWorth = assets - debt

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFAD88C6)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Net Worth",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )

            Text(
                text = formatRupiah(netWorth),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Assets", color = Color.White)
                    Text(formatRupiah(assets), color = Color.White)
                }

                Column {
                    Text("Debt", color = Color.White)
                    Text(formatRupiah(debt), color = Color.White)
                }
            }
        }
    }
}