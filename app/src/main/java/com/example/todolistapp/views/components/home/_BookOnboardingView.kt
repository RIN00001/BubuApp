package com.example.todolistapp.views.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.todolistapp.viewModels.BookViewModel
import com.example.todolistapp.uiStates.BookMutationStatusUIState
import com.example.todolistapp.views.components.home._BookAddForm


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookOnboardingView(
    bookViewModel: BookViewModel,
    onDone: () -> Unit
) {
    val mutationState by bookViewModel.mutationState.collectAsState()

    LaunchedEffect(mutationState) {
        if (mutationState is BookMutationStatusUIState.Success) {
            onDone()
            bookViewModel.resetMutationState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Buat Buku Pertama") }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier.padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            _BookAddForm(
                onSubmit = { name, program ->
                    bookViewModel.createBook(
                        name = name,
                        program = program,
                        walletIds = null
                    )
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = {
                    bookViewModel.createBook(
                        name = "Buku Bawaan",
                        program = "Default",
                        walletIds = null
                    )
                }
            ) {
                Text("Lewati (Gunakan Buku Bawaan)")
            }
        }
    }
}
