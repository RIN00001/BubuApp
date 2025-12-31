package com.example.todolistapp.views.components.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.BookModel
import com.example.todolistapp.uiStates.BookListStatusUIState
import com.example.todolistapp.viewModels.BookViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun PreviewBooksList() {
    BooksList(
        navController = rememberNavController()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksList(
    navController: NavHostController,
    bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val listState by bookViewModel.listState.collectAsState()
    var bookToDelete by remember { mutableStateOf<BookModel?>(null) }
    var bookToEdit by remember { mutableStateOf<BookModel?>(null) }

    LaunchedEffect(Unit) {
        bookViewModel.fetchBooks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Books List",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9B8FC7)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(PagesEnum.BookCreate.name)
                },
                containerColor = Color(0xFFAD88C6),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Book",
                    tint = Color.White
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFE6E6))
                .padding(padding)
        ) {
            when (listState) {
                is BookListStatusUIState.Loading,
                is BookListStatusUIState.Start -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }

                is BookListStatusUIState.Success -> {
                    val books = (listState as BookListStatusUIState.Success).data

                    if (books.isEmpty()) {
                        Text(
                            text = "No books yet. Create one!",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(books) { book ->
                                SwipeableBookListCard(
                                    book = book,
                                    onClick = {
                                        navController.navigate(
                                            PagesEnum.BookDetail.name + "/${book.id}"
                                        )
                                    },
                                    onEdit = {
                                        bookToEdit = book
                                    },
                                    onDelete = {
                                        bookToDelete = book
                                    }
                                )
                            }
                        }
                    }
                }

                is BookListStatusUIState.Failed -> {
                    Text(
                        text = "Failed to load books",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Delete Dialog
        bookToDelete?.let { book ->
            _BookDelete(
                bookName = book.name,
                onConfirm = {
                    bookViewModel.deleteBook(book.id)
                    bookToDelete = null
                },
                onDismiss = {
                    bookToDelete = null
                }
            )
        }

        // Edit Dialog
        bookToEdit?.let { book ->
            _BookEditForm(
                book = book,
                onSubmit = { name, program ->
                    bookViewModel.updateBook(book.id, name, program, null)
                    bookToEdit = null
                },
                onDismiss = {
                    bookToEdit = null
                }
            )
        }
    }
}
