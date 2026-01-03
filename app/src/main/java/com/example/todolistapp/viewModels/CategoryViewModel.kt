package com.example.todolistapp.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.BubuApplication // Fix Import
import com.example.todolistapp.models.CategoryModel
import com.example.todolistapp.repositories.CategoryRepositoryInterface
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepositoryInterface
) : ViewModel() {

    // --- State UI ---
    var categories by mutableStateOf<List<CategoryModel>>(emptyList())
        private set // Supaya hanya bisa diubah di dalam ViewModel

    var isLoading by mutableStateOf(false)
        private set

    // Tab Aktif (Default: EXPENSE)
    var currentTab by mutableStateOf("EXPENSE")
        private set

    init {
        loadCategories()
    }

    // 1. Ganti Tab (Dipanggil saat user klik Tab Pemasukan/Pengeluaran)
    fun onTabSelected(type: String) {
        currentTab = type
        loadCategories()
    }

    // 2. Load Data
    fun loadCategories() {
        isLoading = true
        viewModelScope.launch {
            try {
                // Request ke API dengan filter TYPE (INCOME/EXPENSE)
                val response = categoryRepository.getAllCategories(currentTab)

                if (response.isSuccessful) {
                    val allData = response.body()?.data ?: emptyList()
                    // Filter sisi client untuk keamanan ganda
                    categories = allData.filter { it.type == currentTab }
                } else {
                    Log.e("CAT_VM", "Gagal load: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CAT_VM", "Error load: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // 3. Create Data
    fun addCategory(context: Context, name: String, icon: String = "default") {
        if (name.isBlank()) {
            Toast.makeText(context, "Nama kategori wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // Buat kategori sesuai Tab yang sedang aktif
                val response = categoryRepository.createCategory(name, currentTab, icon)

                if (response.isSuccessful) {
                    Toast.makeText(context, "Kategori berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    loadCategories() // Refresh list
                } else {
                    val msg = response.errorBody()?.string() ?: "Gagal membuat kategori"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    // 4. Update Data
    fun updateCategory(context: Context, id: Int, name: String, icon: String = "default") {
        if (name.isBlank()) {
            Toast.makeText(context, "Nama kategori wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val response = categoryRepository.updateCategory(id, name, currentTab, icon)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Berhasil diupdate!", Toast.LENGTH_SHORT).show()
                    loadCategories()
                } else {
                    Toast.makeText(context, "Gagal update", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    // 5. Delete Data
    fun deleteCategory(context: Context, categoryId: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = categoryRepository.deleteCategory(categoryId)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Kategori dihapus", Toast.LENGTH_SHORT).show()
                    loadCategories()
                } else {
                    Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    // --- FACTORY ---
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // FIX: Gunakan BubuApplication
                val app = (this[APPLICATION_KEY] as BubuApplication)
                CategoryViewModel(
                    categoryRepository = app.container.categoryRepository
                )
            }
        }
    }
}