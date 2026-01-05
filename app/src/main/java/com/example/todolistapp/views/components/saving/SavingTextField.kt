package com.example.todolistapp.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SavingTextField(
    inputValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    labelText: String,
    placeholderText: String,
    minLine: Int = 1,
    maxLine: Int = 1
) {
    OutlinedTextField(
        value = inputValue,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(labelText) },
        placeholder = { Text(placeholderText) },
        minLines = minLine,
        maxLines = maxLine
    )
}
