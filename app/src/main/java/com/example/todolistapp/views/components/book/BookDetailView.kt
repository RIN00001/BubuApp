package com.example.todolistapp.views.components.book

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.uiStates.BookDetailStatusUIState
import com.example.todolistapp.viewModels.BookViewModel


import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun PreviewBookDetailView() {
    // You may need to provide a fake BookViewModel or mock state for a full preview
    BookDetailView(
        bookId = 1,
        navController = rememberNavController()
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailView(
    bookId: Int,
    navController: NavHostController,
    bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val detailState by bookViewModel.detailState.collectAsState()

    LaunchedEffect(bookId) {
        bookViewModel.fetchBookDetail(bookId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Book Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("<")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            when (detailState) {

                is BookDetailStatusUIState.Loading,
                is BookDetailStatusUIState.Start -> {
                    CircularProgressIndicator()
                }

                is BookDetailStatusUIState.Success -> {
                    val book = (detailState as BookDetailStatusUIState.Success).data

                    Text(
                        text = book.name,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = book.program ?: "No program",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Wallets",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (book.wallets.isNullOrEmpty()) {
                        Text("No wallets in this book")
                    } else {
                        book.wallets.forEach { wallet ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(wallet.name)
                                    Text("Balance: ${wallet.balance}")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            bookViewModel.deleteBook(book.id)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Book")
                    }
                }

                is BookDetailStatusUIState.Failed -> {
                    Text("Failed to load book detail")
                }
            }
        }
    }
}