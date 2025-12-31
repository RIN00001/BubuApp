package com.example.todolistapp.views.components.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (initialWallet != null) "Edit Wallet Information" else "New Wallet Information",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Wallet Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = balanceText,
            onValueChange = { balanceText = it },
            label = { Text("Balance") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val balance = balanceText.toDoubleOrNull() ?: 0.0
                if (name.isNotBlank()) {
                    onSubmit(name, balance)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9B8FC7)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                if (initialWallet != null) "Update Wallet" else "Create Wallet",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
