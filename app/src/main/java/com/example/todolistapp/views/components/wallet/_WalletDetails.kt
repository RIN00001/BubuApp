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
fun _WalletDetails(
    wallet: WalletModel,
    onSetDefault: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFE6E6)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = wallet.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatRupiah(wallet.balance),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF7469B6)
                )

                if (wallet.isDefault) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Default Wallet",
                        color = Color(0xFF7469B6),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!wallet.isDefault) {
            Button(
                onClick = onSetDefault,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFAD88C6)
                )
            ) {
                Text("Set as Default")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Wallet")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Delete Wallet")
        }
    }
}