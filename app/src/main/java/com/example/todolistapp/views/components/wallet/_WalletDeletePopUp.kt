package com.example.todolistapp.views.components.wallet

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun _WalletDeletePopUp(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Wallet") },
        text = { Text("Are you sure you want to delete this wallet?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}