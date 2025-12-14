package com.example.todolistapp.views.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.BookListStatusUIState
import com.example.todolistapp.viewModels.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksList(
    navController: NavHostController,
    bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val listState by bookViewModel.listState.collectAsState()

    LaunchedEffect(Unit) {
        bookViewModel.fetchBooks()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Books List") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(PagesEnum.BookCreate.name)
                }
            ) {
                Text("+")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            when (listState) {
                is BookListStatusUIState.Loading,
                is BookListStatusUIState.Start -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                }

                is BookListStatusUIState.Success -> {
                    val books = (listState as BookListStatusUIState.Success).data

                    books.forEach { book ->
                        BookListCard(
                            book = book,
                            onClick = {
                                navController.navigate(
                                    PagesEnum.BookDetail.name + "/${book.id}"
                                )
                            },
                            onDelete = {
                                bookViewModel.deleteBook(book.id)
                            }
                        )
                    }
                }

                is BookListStatusUIState.Failed -> {
                    Text(
                        text = "Failed to load books",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
