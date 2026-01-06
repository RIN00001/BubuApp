package com.example.todolistapp.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.models.ItemModel
import com.example.todolistapp.uiStates.ItemListStatusUIState
import com.example.todolistapp.uiStates.ItemMutationStatusUIState
import com.example.todolistapp.viewModels.ItemViewModel
import com.example.todolistapp.views.components.item._ItemAddForm
import com.example.todolistapp.views.components.item._ItemEditForm
import com.example.todolistapp.views.components.item._ItemDelete
import com.example.todolistapp.views.components.item.SwipeableItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsList(
    navController: NavHostController,
    bookId: Int,
    itemViewModel: ItemViewModel = viewModel(factory = ItemViewModel.Factory)
) {
    // 1. State Data Transaksi
    val listState by itemViewModel.listState.collectAsState()
    val mutationState by itemViewModel.mutationState.collectAsState()

    // 2. State Data Dropdown (Wallet & Category)
    val walletList by itemViewModel.walletDropdownState.collectAsState()
    val categoryList by itemViewModel.categoryDropdownState.collectAsState()

    // 3. State Dialogs
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ItemModel?>(null) }
    var itemToDelete by remember { mutableStateOf<ItemModel?>(null) }

    val context = LocalContext.current

    // 4. Fetch Data saat halaman dibuka
    LaunchedEffect(bookId) {
        itemViewModel.fetchItemsByBook(bookId)
        itemViewModel.fetchWalletsForDropdown()
        itemViewModel.fetchCategoriesForDropdown()
    }

    // 5. Handle Feedback (Sukses/Gagal Simpan)
    LaunchedEffect(mutationState) {
        when(val state = mutationState) {
            is ItemMutationStatusUIState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                itemViewModel.resetMutationState()
                // Refresh data
                itemViewModel.fetchItemsByBook(bookId)
                itemViewModel.fetchWalletsForDropdown()
            }
            is ItemMutationStatusUIState.Failed -> {
                Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                itemViewModel.resetMutationState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Daftar Transaksi",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF9B8FC7)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFAD88C6),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color.White)
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            when (listState) {
                is ItemListStatusUIState.Loading, is ItemListStatusUIState.Start -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF9B8FC7)
                    )
                }
                is ItemListStatusUIState.Success -> {
                    val items = (listState as ItemListStatusUIState.Success).data

                    if (items.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Belum ada transaksi.",
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Tekan + untuk menambah.",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items) { item ->
                                SwipeableItemCard(
                                    item = item,
                                    onClick = { itemToEdit = item },
                                    onEdit = { itemToEdit = item },
                                    onDelete = { itemToDelete = item }
                                )
                            }
                        }
                    }
                }
                is ItemListStatusUIState.Failed -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Gagal memuat data", color = Color.Red)
                        Button(onClick = { itemViewModel.fetchItemsByBook(bookId) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }

        // ==========================================
        // DIALOG SECTIONS
        // ==========================================

        // 1. Dialog Tambah
        if (showAddDialog) {
            Dialog (onDismissRequest = { showAddDialog = false }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    _ItemAddForm(
                        wallets = walletList,
                        categories = categoryList,
                        onSubmit = { name, amount, type, walletId, categoryId ->
                            itemViewModel.createItem(name, amount, type, bookId, walletId, categoryId)
                            showAddDialog = false
                        }
                    )
                }
            }
        }

        // 2. Dialog Edit
        itemToEdit?.let { item ->
            _ItemEditForm(
                item = item,
                wallets = walletList,
                categories = categoryList,
                onSubmit = { name, amount, type, walletId, categoryId ->
                    itemViewModel.updateItem(
                        item.id, name, amount, type, bookId, walletId, categoryId
                    )
                    itemToEdit = null
                },
                onDismiss = { itemToEdit = null }
            )
        }

        // 3. Dialog Delete
        itemToDelete?.let { item ->
            _ItemDelete(
                itemName = item.name,
                onConfirm = {
                    itemViewModel.deleteItem(item.id, bookId)
                    itemToDelete = null
                },
                onDismiss = { itemToDelete = null }
            )
        }
    }
}