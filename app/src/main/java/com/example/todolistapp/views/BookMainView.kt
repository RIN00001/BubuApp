package com.example.todolistapp.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.bubuapp.views.components.NavigationBar
import com.example.todolistapp.enums.PagesEnum
// Import UI States
import com.example.todolistapp.uiStates.BookListStatusUIState
import com.example.todolistapp.uiStates.ItemListStatusUIState
import com.example.todolistapp.uiStates.ItemMutationStatusUIState
// Import ViewModels
import com.example.todolistapp.viewModels.BookViewModel
import com.example.todolistapp.viewModels.ItemViewModel
// Import Components
import com.example.todolistapp.views.components.book.BookMainHeader
import com.example.todolistapp.views.components.book.BookSummaryCard
import com.example.todolistapp.views.components.item.SwipeableItemCard
import com.example.todolistapp.views.components.item._ItemAddForm
import com.example.todolistapp.views.components.item._ItemEditForm
// Import Models
import com.example.todolistapp.models.ItemModel

@Composable
fun BookMainView(
    navController: NavHostController,
    bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory),
    itemViewModel: ItemViewModel = viewModel(factory = ItemViewModel.Factory)
) {
    val context = LocalContext.current

    // --- STATE BUKU ---
    val bookListState by bookViewModel.listState.collectAsState()
    var selectedBookId by remember { mutableStateOf<Int?>(null) }
    var hideValues by remember { mutableStateOf(false) }

    // --- STATE ITEM / TRANSAKSI ---
    val itemListState by itemViewModel.listState.collectAsState()
    val itemMutationState by itemViewModel.mutationState.collectAsState()

    // State Dropdown (Wallet & Category)
    val walletList by itemViewModel.walletDropdownState.collectAsState()
    val categoryList by itemViewModel.categoryDropdownState.collectAsState()

    // --- STATE DIALOG ---
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ItemModel?>(null) }
    var itemToDelete by remember { mutableStateOf<ItemModel?>(null) }

    // 1. Load Buku saat pertama buka
    LaunchedEffect(Unit) {
        bookViewModel.fetchBooks()
    }

    // 2. Logic Penentuan Buku Aktif
    // Jika user belum pilih (null), pakai buku pertama dari list
    val books = (bookListState as? BookListStatusUIState.Success)?.data
    val currentBookId = selectedBookId ?: books?.firstOrNull()?.id

    // Jika Buku Berubah -> Load Transaksi & Dropdown terkait
    LaunchedEffect(currentBookId) {
        if (currentBookId != null) {
            itemViewModel.fetchItemsByBook(currentBookId)
            itemViewModel.fetchWalletsForDropdown()
            itemViewModel.fetchCategoriesForDropdown()

            // Sinkronkan state lokal jika masih null
            if (selectedBookId == null) selectedBookId = currentBookId
        }
    }

    // 3. HANDLE FEEDBACK (Sukses/Gagal Simpan) & REFRESH REALTIME
    LaunchedEffect(itemMutationState) {
        when (val state = itemMutationState) {
            is ItemMutationStatusUIState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                itemViewModel.resetMutationState()

                // Tutup Dialog
                showAddDialog = false
                itemToEdit = null
                itemToDelete = null

                // --- UPDATE SEMUA DATA AGAR REALTIME ---
                if (currentBookId != null) {
                    // a. Update List Transaksi (Bawah)
                    itemViewModel.fetchItemsByBook(currentBookId)
                    // b. Update Saldo Wallet di Dropdown (Opsional tapi bagus)
                    itemViewModel.fetchWalletsForDropdown()
                }

                // c. [PENTING] Update Summary Buku (Card Atas: Income/Expense/Balance)
                bookViewModel.fetchBooks()
            }
            is ItemMutationStatusUIState.Failed -> {
                Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                itemViewModel.resetMutationState()
            }
            else -> {}
        }
    }

    Scaffold(
        bottomBar = { NavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentBookId != null) {
                        showAddDialog = true
                    } else {
                        Toast.makeText(context, "Silakan buat buku terlebih dahulu", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = Color(0xFFAD88C6),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Item", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFE6E6)) // Background Merah Muda Soft sesuai desainmu
                .padding(innerPadding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp), // Beri ruang agar tidak tertutup FAB
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- BAGIAN 1: HEADER & SUMMARY CARD ---
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        BookMainHeader(
                            listState = bookListState,
                            selectedBookId = selectedBookId,
                            onBookSelected = { selectedBookId = it },
                            onDropdownClick = { navController.navigate("BooksList") },
                            onAddBookClick = { navController.navigate(PagesEnum.BookCreate.name) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val selectedBook = books?.find { it.id == currentBookId }
                        if (selectedBook != null) {
                            BookSummaryCard(
                                book = selectedBook,
                                hideValues = hideValues,
                                onToggleVisibility = { hideValues = !hideValues }
                            )
                        }
                    }
                }

                // --- BAGIAN 2: LIST TRANSAKSI ---
                when (itemListState) {
                    is ItemListStatusUIState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is ItemListStatusUIState.Success -> {
                        val items = (itemListState as ItemListStatusUIState.Success).data

                        if (items.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Belum ada transaksi", color = Color.Gray)
                                    Text("Tekan + untuk menambah", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        } else {
                            items(items) { item ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    SwipeableItemCard(
                                        item = item,
                                        onClick = { /* Opsional: Detail Item */ },
                                        onEdit = { itemToEdit = item },
                                        onDelete = { itemToDelete = item }
                                    )
                                }
                            }
                        }
                    }
                    is ItemListStatusUIState.Failed -> {
                        item {
                            Text("Gagal memuat transaksi", color = Color.Red, modifier = Modifier.padding(16.dp))
                        }
                    }
                    else -> {}
                }
            }
        }

        // ==========================================
        // DIALOGS SECTION (POPUP)
        // ==========================================

        // 1. DIALOG ADD (TAMBAH)
        if (showAddDialog && currentBookId != null) {
            Dialog(
                onDismissRequest = { showAddDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.95f).padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        Text(
                            text = "Tambah Transaksi Baru",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                        _ItemAddForm(
                            wallets = walletList,
                            categories = categoryList,
                            onSubmit = { name, amount, type, walletId, categoryId ->
                                itemViewModel.createItem(
                                    name, amount, type, currentBookId, walletId, categoryId
                                )
                            }
                        )
                        TextButton(
                            onClick = { showAddDialog = false },
                            modifier = Modifier.align(Alignment.End).padding(end = 8.dp, bottom = 8.dp)
                        ) {
                            Text("Batal")
                        }
                    }
                }
            }
        }

        // 2. DIALOG EDIT
        itemToEdit?.let { item ->
            _ItemEditForm(
                item = item,
                wallets = walletList,
                categories = categoryList,
                onSubmit = { name, amount, type, walletId, categoryId ->
                    if (currentBookId != null) {
                        itemViewModel.updateItem(
                            item.id, name, amount, type, currentBookId, walletId, categoryId
                        )
                    }
                },
                onDismiss = { itemToEdit = null }
            )
        }

        // 3. DIALOG DELETE CONFIRMATION
        itemToDelete?.let { item ->
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("Hapus Transaksi?") },
                text = { Text("Apakah Anda yakin ingin menghapus '${item.name}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            if (currentBookId != null) {
                                itemViewModel.deleteItem(item.id, currentBookId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}