package com.example.todolistapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DatePicker(
    datePickerValue: String,
    showCalendarDialog: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    // Extract only the date part (YYYY-MM-DD) from the datePickerValue
    val displayDate = if (datePickerValue.isNotEmpty()) {
        datePickerValue.split("T")[0] // Remove timestamp if exists
    } else {
        ""
    }

    OutlinedTextField(
        value = displayDate,
        onValueChange = {},
        modifier = modifier,
        label = { Text("Tanggal Target") },
        placeholder = { Text("Pilih tanggal") },
        enabled = false,
        trailingIcon = {
            Button(
                onClick = showCalendarDialog,
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFAD88C6)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("📅", color = Color.White)
            }
        }
    )
}
