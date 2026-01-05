package com.example.todolistapp.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DatePicker(
    datePickerValue: String,
    showCalendarDialog: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = datePickerValue,
        onValueChange = {},
        modifier = modifier,
        label = { Text("Due Date") },
        placeholder = { Text("Select due date") },
        enabled = false,
        trailingIcon = {
            Button(onClick = showCalendarDialog) {
                Text("📅")
            }
        }
    )
}
