package com.example.todolistapp.views.components.wallet


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolistapp.models.WalletModel

@Composable
fun _WalletAddForm(
    initialWallet: WalletModel? = null,
    onSubmit: (name: String, balance: Double) -> Unit
) {
    var name by remember { mutableStateOf(initialWallet?.name ?: "") }
    var balanceText by remember {
        mutableStateOf(initialWallet?.balance?.toInt()?.toString() ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Wallet Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = balanceText,
            onValueChange = { balanceText = it },
            label = { Text("Initial Balance") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val balance = balanceText.toDoubleOrNull() ?: 0.0
                if (name.isNotBlank()) {
                    onSubmit(name, balance)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Wallet")
        }
    }
}
