package com.example.todolistapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.bubuapp.views.components.NavigationBar // Pastikan import ini sesuai dengan projectmu
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.BookListStatusUIState
import com.example.todolistapp.viewModels.BookViewModel
import com.example.todolistapp.views.components.book.BookMainHeader
import com.example.todolistapp.views.components.book.BookSummaryCard
import com.example.todolistapp.views.components.book.error.RecentItemsPlaceholder

@Composable
fun BookMainView(
    navController: NavHostController,
    bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val listState by bookViewModel.listState.collectAsState()

    // State untuk menampung ID buku yang dipilih user lewat Dropdown
    var selectedBookId by remember { mutableStateOf<Int?>(null) }

    var hideValues by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bookViewModel.fetchBooks()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // === PERBAIKAN LOGIC DI SINI ===
                    // 1. Ambil data buku yang sudah load
                    val books = (listState as? BookListStatusUIState.Success)?.data

                    // 2. Tentukan Buku Aktif:
                    // Jika user memilih lewat dropdown -> pakai selectedBookId
                    // Jika tidak -> pakai buku pertama di list (default)
                    val currentId = selectedBookId ?: books?.firstOrNull()?.id

                    // 3. Navigasi ke halaman Transaksi (Item)
                    if (currentId != null) {
                        navController.navigate("ItemsList/$currentId")
                    }
                },
                containerColor = Color(0xFFAD88C6),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Add Item",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFE6E6))
        ) {
            // Custom Top Header
            BookMainHeader(
                listState = listState,
                selectedBookId = selectedBookId,
                onBookSelected = { selectedBookId = it }, // Update state saat dropdown berubah
                onDropdownClick = {
                    navController.navigate("BooksList")
                },
                onAddBookClick = {
                    navController.navigate(PagesEnum.BookCreate.name)
                }
            )

            // Main Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Card
                item {
                    when (listState) {
                        is BookListStatusUIState.Success -> {
                            val books = (listState as BookListStatusUIState.Success).data

                            // Tampilkan data sesuai logic yang sama (pilihan user atau default pertama)
                            val selectedBook = books.find { it.id == selectedBookId } ?: books.firstOrNull()

                            if (selectedBook != null) {
                                // Update selectedBookId jika masih null agar sinkron dengan FAB
                                if (selectedBookId == null) {
                                    selectedBookId = selectedBook.id
                                }

                                BookSummaryCard(
                                    book = selectedBook,
                                    hideValues = hideValues,
                                    onToggleVisibility = { hideValues = !hideValues }
                                )
                            }
                        }
                        else -> {
                            // Show loading or placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.White, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                item {
                    RecentItemsPlaceholder()
                }
            }
        }
    }
}