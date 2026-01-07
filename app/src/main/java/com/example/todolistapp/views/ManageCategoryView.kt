package com.example.todolistapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistapp.models.CategoryModel
import com.example.todolistapp.utils.CategoryIconHelper
import com.example.todolistapp.viewModels.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoryView(
    onBackClick: () -> Unit,
    viewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory)
) {
    val context = LocalContext.current
    val tabs = listOf("Pengeluaran", "Pemasukan")
    val selectedTabIndex = if (viewModel.currentTab == "EXPENSE") 0 else 1

    // State untuk Dialog
    var showDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryModel?>(null) }
    var inputName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kategori", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFEADDFF) // Warna ungu muda
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    categoryToEdit = null
                    inputName = ""
                    showDialog = true
                },
                containerColor = Color(0xFF6750A4),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFDF7FF)) // Background putih sedikit ungu
        ) {
            // --- TAB ROW ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFF6750A4)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            viewModel.onTabSelected(if (index == 0) "EXPENSE" else "INCOME")
                        },
                        text = { Text(title) }
                    )
                }
            }

            // --- LIST CONTENT ---
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.categories) { category ->
                        CategoryItemCard(
                            category = category,
                            onEdit = {
                                categoryToEdit = category
                                inputName = category.name
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

        // --- DIALOG ADD / EDIT ---
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (categoryToEdit == null) "Tambah Kategori" else "Edit Kategori",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            label = { Text("Nama Kategori") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Batal")
                            }
                            Button(
                                onClick = {
                                    if (categoryToEdit == null) {
                                        viewModel.addCategory(context, inputName)
                                    } else {
                                        // Saat edit, kita pertahankan icon lama
                                        val currentIcon = categoryToEdit?.iconKey ?: "default"
                                        viewModel.updateCategory(context, categoryToEdit!!.id, inputName, currentIcon)
                                    }
                                    showDialog = false
                                }
                            ) {
                                Text("Simpan")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItemCard(
    category: CategoryModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- BAGIAN ICON (DINAMIS) ---
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEADDFF)), // Lingkaran ungu muda
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    // MEMANGGIL HELPER ICON
                    imageVector = CategoryIconHelper.getIcon(category.iconKey),
                    contentDescription = null,
                    tint = Color(0xFF21005D), // Ungu tua
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            // Tombol Aksi
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}