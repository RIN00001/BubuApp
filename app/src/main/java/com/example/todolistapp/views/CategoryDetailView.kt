package com.example.todolistapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.todolistapp.models.ItemModel
// PERBAIKAN IMPORT: Ambil dari uiStates, bukan viewModels
import com.example.todolistapp.uiStates.HomeUIState
import com.example.todolistapp.viewModels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailView(
    navController: NavHostController,
    viewModel: HomeViewModel,
    categoryId: Int,
    categoryName: String
) {
    val uiState = viewModel.homeUIState
    val lifecycleOwner = LocalLifecycleOwner.current

    // Load data setiap kali halaman tampil (Refresh saat kembali dari halaman Add/Edit)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getAllItems()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Bersihkan state edit sebelum navigasi ke Create (Mode Tambah)
                navController.currentBackStackEntry?.savedStateHandle?.remove<Int>("edit_id")
                // Navigasi dengan parameter categoryId agar otomatis terpilih
                navController.navigate("create?categoryId=$categoryId")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (uiState) {
                is HomeUIState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HomeUIState.Error -> {
                    Text("Error memuat data / Gagal koneksi", modifier = Modifier.align(Alignment.Center))
                }
                is HomeUIState.Success -> {
                    // Filter item sesuai categoryId yang sedang dibuka
                    val filteredItems = uiState.items.filter { it.categoryId == categoryId }

                    if (filteredItems.isEmpty()) {
                        Text("Belum ada transaksi di kategori ini.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredItems) { item ->
                                TransactionItemCard(
                                    item = item,
                                    onDelete = { viewModel.deleteItem(item.id) },
                                    onEdit = {
                                        // Simpan data ke SavedStateHandle untuk mode Edit
                                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                                            set("edit_id", item.id)
                                            set("edit_name", item.name)
                                            set("edit_amount", item.amount)
                                            set("edit_type", item.type)
                                            set("edit_cat_id", item.categoryId) // Opsional: kirim cat id juga
                                        }
                                        navController.navigate("create?categoryId=$categoryId")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItemCard(item: ItemModel, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)

                // Format amount biar lebih rapi (Misal warna merah expense, hijau income)
                val color = if (item.type == "EXPENSE") Color.Red else Color(0xFF006400) // Dark Green
                Text(
                    text = "Rp ${item.amount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}