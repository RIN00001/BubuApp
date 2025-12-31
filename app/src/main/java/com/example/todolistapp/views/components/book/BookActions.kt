package com.example.todolistapp.views.components.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun BookQuickActions() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AssistChip(
            onClick = { /* TODO add book */ },
            label = { Text("Add Book") }
        )
    }
}