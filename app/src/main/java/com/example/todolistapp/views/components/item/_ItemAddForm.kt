package com.example.todolistapp.views.components.item

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.todolistapp.models.WalletModel // Pastikan import Model Wallet ada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun _ItemAddForm(
    wallets: List<WalletModel>, // [BARU] Terima list wallet
    onSubmit: (name: String, amount: Double, type: String, walletId: Int?) -> Unit // [BARU] Kirim walletId
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("EXPENSE") }

    // State untuk Dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedWallet by remember { mutableStateOf<WalletModel?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // 1. Input Nama
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Transaksi") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Input Nominal
        OutlinedTextField(
            value = amount,
            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amount = it },
            label = { Text("Nominal (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            prefix = { Text("Rp ") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. [BARU] Dropdown Wallet
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedWallet?.name ?: "Pilih Sumber Dana (Wallet)",
                onValueChange = {},
                readOnly = true,
                label = { Text("Wallet") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                if (wallets.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Tidak ada wallet tersedia") },
                        onClick = { expanded = false }
                    )
                } else {
                    wallets.forEach { wallet ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(wallet.name, style = MaterialTheme.typography.bodyLarge)
                                    Text("Sisa: Rp${wallet.balance}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            },
                            onClick = {
                                selectedWallet = wallet
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // Helper text jika wallet belum dipilih
        if (selectedWallet == null) {
            Text(
                text = "* Wajib pilih wallet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Pilihan Tipe (Income/Expense)
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("EXPENSE", "INCOME").forEach { type ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = (selectedType == type),
                            onClick = { selectedType = type }
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedType == type),
                        onClick = { selectedType = type }
                    )
                    Text(
                        text = if(type == "EXPENSE") "Pengeluaran" else "Pemasukan",
                        color = if(type == "EXPENSE" && selectedType == type) Color.Red
                        else if(type == "INCOME" && selectedType == type) Color(0xFF006400)
                        else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val amountVal = amount.toDoubleOrNull()
                // Validasi: Nama, Amount, dan Wallet harus terisi
                if (name.isNotBlank() && amountVal != null && selectedWallet != null) {
                    onSubmit(name, amountVal, selectedType, selectedWallet!!.id)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && amount.isNotBlank() && selectedWallet != null
        ) {
            Text("Simpan Transaksi")
        }
    }
}