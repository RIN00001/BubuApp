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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistapp.models.CategoryModel
import com.example.todolistapp.viewModels.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoryView(
    onBackClick: () -> Unit,
    viewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory)
) {
    val context = LocalContext.current
    val categories = viewModel.categories
    val isLoading = viewModel.isLoading
    val currentTab = viewModel.currentTab

    var showDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryModel?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Kategori") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                categoryToEdit = null // Mode Tambah
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kategori")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Tab Pilihan (Income / Expense)
            TabRow(selectedTabIndex = if (currentTab == "EXPENSE") 0 else 1) {
                Tab(
                    selected = currentTab == "EXPENSE",
                    onClick = { viewModel.onTabSelected("EXPENSE") },
                    text = { Text("Pengeluaran") }
                )
                Tab(
                    selected = currentTab == "INCOME",
                    onClick = { viewModel.onTabSelected("INCOME") },
                    text = { Text("Pemasukan") }
                )
            }

            // Loading & List
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (categories.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada kategori untuk tipe ini.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            CategoryItemCard(
                                category = category,
                                onEdit = {
                                    categoryToEdit = category
                                    showDialog = true
                                },
                                onDelete = {
                                    viewModel.deleteCategory(context, category.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Dialog Input
        if (showDialog) {
            // 2. PERBAIKAN: Menampilkan Tipe di Judul Dialog agar jelas
            val typeLabel = if (currentTab == "EXPENSE") "Pengeluaran" else "Pemasukan"

            CategoryInputDialog(
                title = if (categoryToEdit != null) "Edit Kategori" else "Tambah Kategori ($typeLabel)",
                initialName = categoryToEdit?.name ?: "",
                onDismiss = { showDialog = false },
                onConfirm = { name ->
                    if (categoryToEdit != null) {
                        // Update Logic
                        viewModel.updateCategory(context, categoryToEdit!!.id, name)
                    } else {
                        // Create Logic (Otomatis menggunakan currentTab dari ViewModel)
                        viewModel.addCategory(context, name)
                    }
                    showDialog = false
                }
            )
        }
    }
}

// --- Komponen Card ---
@Composable
fun CategoryItemCard(
    category: CategoryModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Tampilkan Icon Folder default atau dari API
                Text(
                    text = if (category.iconKey == "default") "📁" else category.iconKey ?: "📁",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    // Tampilkan Type kecil di bawah nama (Opsional, untuk debug)
                    Text(
                        text = category.type,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// --- Komponen Dialog ---
@Composable
fun CategoryInputDialog(
    title: String,
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Nama Kategori") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}