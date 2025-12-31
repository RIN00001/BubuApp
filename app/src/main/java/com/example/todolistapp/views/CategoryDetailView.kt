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
import com.example.todolistapp.viewModels.HomeUIState
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

    // Load data setiap kali halaman tampil
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
                navController.currentBackStackEntry?.savedStateHandle?.remove<Int>("edit_id")
                // Navigasi dengan parameter categoryId
                navController.navigate("create?categoryId=$categoryId")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (uiState) {
                is HomeUIState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is HomeUIState.Error -> Text("Error memuat data", modifier = Modifier.align(Alignment.Center))
                is HomeUIState.Success -> {
                    // PERBAIKAN DISINI: Gunakan 'it.categoryId' (sesuai nama variabel di ItemModel)
                    val filteredItems = uiState.items.filter { it.categoryId == categoryId }

                    if (filteredItems.isEmpty()) {
                        Text("Belum ada transaksi di sini.", modifier = Modifier.align(Alignment.Center))
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
                                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                                            set("edit_id", item.id)
                                            set("edit_name", item.name)
                                            set("edit_amount", item.amount) // amount sudah Long, tidak perlu toLong() lagi sebenarnya
                                            set("edit_type", item.type)
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
                // item.amount di ItemModel kamu sudah Long, jadi langsung saja
                Text("Rp ${item.amount}", style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit", tint = Color.Blue) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = Color.Red) }
            }
        }
    }
}