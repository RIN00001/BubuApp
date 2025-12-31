package com.example.todolistapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.viewModels.CreateItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemView(
    navController: NavHostController,
    viewModel: CreateItemViewModel = viewModel(factory = CreateItemViewModel.Factory)
) {
    val context = LocalContext.current

    // 1. Ambil Argument categoryId dari Navigasi (Jika masuk dari dalam folder)
    val navBackStackEntry = navController.currentBackStackEntry
    val defaultCategoryId = navBackStackEntry?.arguments?.getInt("categoryId") ?: -1

    // State untuk Dropdown
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Load daftar kategori agar bisa dipilih di dropdown
        viewModel.loadCategories()

        // --- CEK MODE EDIT ---
        val savedState = navController.previousBackStackEntry?.savedStateHandle
        if (savedState != null && savedState.contains("edit_id")) {
            val id = savedState.get<Int>("edit_id") ?: 0
            val name = savedState.get<String>("edit_name") ?: ""
            val amount = savedState.get<Long>("edit_amount") ?: 0L
            val type = savedState.get<String>("edit_type") ?: "EXPENSE"

            // TAMBAHAN: Ambil Date dan Category ID (atau default value jika tidak dikirim)
            val date = savedState.get<String>("edit_date") ?: ""
            val catId = savedState.get<Int>("edit_cat_id") ?: defaultCategoryId

            // FIX: Panggil dengan 6 Parameter sesuai ViewModel terbaru
            viewModel.setupEditMode(id, name, amount, type, date, catId)
        }

        // JIKA MASUK DARI FOLDER (defaultCategoryId != -1) & BUKAN EDIT, SET KATEGORI OTOMATIS
        if (defaultCategoryId != -1 && viewModel.currentItemId == null) {
            viewModel.categoryId = defaultCategoryId
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (viewModel.currentItemId != null) "Edit Transaction" else "Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- FILTER CHIP TYPE ---
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = viewModel.type == "EXPENSE",
                    onClick = { viewModel.type = "EXPENSE" },
                    label = { Text("Expense") },
                    leadingIcon = { if (viewModel.type == "EXPENSE") Icon(Icons.Default.Close, null) }
                )
                FilterChip(
                    selected = viewModel.type == "INCOME",
                    onClick = { viewModel.type = "INCOME" },
                    label = { Text("Income") },
                    leadingIcon = { if (viewModel.type == "INCOME") Icon(Icons.Default.Add, null) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PILIH KATEGORI (DROPDOWN) ---
            // Cari nama kategori yang sedang dipilih berdasarkan ID
            val selectedCategoryName = viewModel.categories.find { it.id == viewModel.categoryId }?.name ?: "Pilih Kategori"

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    // Jika defaultCategoryId != -1, artinya kita dikunci di folder itu (dari detail view), ga bisa ganti
                    // Tapi kalau -1 (dari home "Add"), boleh ganti
                    if (defaultCategoryId == -1) expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    // Disable dropdown jika masuk dari folder (dikunci)
                    enabled = (defaultCategoryId == -1)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Filter dropdown sesuai Tipe (Income/Expense)
                    viewModel.categories.filter { it.type == viewModel.type }.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                viewModel.categoryId = category.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT NAMA ---
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT JUMLAH ---
            OutlinedTextField(
                value = viewModel.amountString,
                onValueChange = { viewModel.amountString = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("Rp ") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL SAVE ---
            Button(
                onClick = { viewModel.saveTransaction(navController, context) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                // Validasi: tidak loading & kategori harus dipilih (tidak boleh 0)
                enabled = !viewModel.isLoading && viewModel.categoryId != 0
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (viewModel.currentItemId != null) "Update Transaction" else "Save Transaction")
                }
            }
        }
    }
}