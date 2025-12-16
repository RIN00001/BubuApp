package com.example.todolistapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.uiStates.BookMutationStatusUIState
import com.example.todolistapp.viewModels.BookViewModel
import com.example.todolistapp.views.components.book._BookAddForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCreateView(
    navController: NavHostController,
    bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val mutationState by bookViewModel.mutationState.collectAsState()

    LaunchedEffect(mutationState) {
        when (mutationState) {
            is BookMutationStatusUIState.Success -> {
                bookViewModel.resetMutationState()
                navController.popBackStack()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create New Book",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9B8FC7)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFE6E6))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    _BookAddForm(
                        onSubmit = { name, program ->
                            bookViewModel.createBook(name, program, null)
                        }
                    )
                }

                // Show loading or error
                when (mutationState) {
                    is BookMutationStatusUIState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is BookMutationStatusUIState.Failed -> {
                        Text(
                            text = "Error: ${(mutationState as BookMutationStatusUIState.Failed).error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

