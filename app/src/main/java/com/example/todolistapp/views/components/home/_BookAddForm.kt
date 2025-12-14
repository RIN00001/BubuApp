package com.example.todolistapp.views.components.home
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun _BookAddForm(
    onSubmit: (name: String, program: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var program by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Book Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = program,
            onValueChange = { program = it },
            label = { Text("Program (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    onSubmit(name, program.ifBlank { null })
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Book")
        }
    }
}
