package com.example.todolistapp.views.components.item

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.todolistapp.models.ItemModel

@Composable
fun _ItemEditForm(
    item: ItemModel,
    onSubmit: (name: String, amount: Double, type: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    // Konversi Double ke String tanpa format saintifik, hilangkan .0 jika bulat
    var amount by remember { mutableStateOf(item.amount.toLong().toString()) }
    var selectedType by remember { mutableStateOf(item.type) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Edit Transaksi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amount = it },
                    label = { Text("Nominal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("Rp ") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Radio Button Type
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("EXPENSE", "INCOME").forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .selectable(selected = (selectedType == type), onClick = { selectedType = type })
                        ) {
                            RadioButton(selected = (selectedType == type), onClick = { selectedType = type })
                            Text(
                                text = if(type == "EXPENSE") "Keluar" else "Masuk",
                                fontSize = 12.sp,
                                color = if(type == "EXPENSE") Color.Red else Color(0xFF006400)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Batal") }

                    Button(
                        onClick = {
                            val amountDouble = amount.toDoubleOrNull() ?: 0.0
                            onSubmit(name, amountDouble, selectedType)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Simpan") }
                }
            }
        }
    }
}