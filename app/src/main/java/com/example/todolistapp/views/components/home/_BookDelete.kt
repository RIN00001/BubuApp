package com.example.todolistapp.views.components.home

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun _BookDelete(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Book") },
        text = { Text("Are you sure you want to delete this book?") },
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
