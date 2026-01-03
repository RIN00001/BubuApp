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

@Composable
fun _ItemAddForm(
    onSubmit: (name: String, amount: Double, type: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("EXPENSE") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Input Nama
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Transaksi") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input Nominal
        OutlinedTextField(
            value = amount,
            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amount = it },
            label = { Text("Nominal (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            prefix = { Text("Rp ") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pilihan Tipe (Income/Expense)
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
                if (name.isNotBlank() && amountVal != null) {
                    onSubmit(name, amountVal, selectedType)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && amount.isNotBlank()
        ) {
            Text("Simpan Transaksi")
        }
    }
}