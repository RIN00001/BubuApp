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
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.models.CategoryModel
import com.example.todolistapp.models.GetAllCategoriesResponse
import com.example.todolistapp.services.CategoryActionResponse
import com.example.todolistapp.repositories.CategoryRepositoryInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel(
    private val categoryRepository: CategoryRepositoryInterface
) : ViewModel() {

    // --- State UI ---
    // Menggunakan State Compose agar UI otomatis update
    var categories by mutableStateOf<List<CategoryModel>>(emptyList())
        private set

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

    // 2. Load Data (MENGGUNAKAN ENQUEUE)
    fun loadCategories() {
        isLoading = true

        // Panggil Repository (yang sekarang mengembalikan Call)
        val call = categoryRepository.getAllCategories(currentTab)

        call.enqueue(object : Callback<GetAllCategoriesResponse> {
            override fun onResponse(
                call: Call<GetAllCategoriesResponse>,
                response: Response<GetAllCategoriesResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val allData = response.body()?.data ?: emptyList()
                    // Filter lagi untuk memastikan tipe sesuai tab
                    categories = allData.filter { it.type == currentTab }
                } else {
                    Log.e("CAT_VM", "Gagal load: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GetAllCategoriesResponse>, t: Throwable) {
                isLoading = false
                Log.e("CAT_VM", "Error connection: ${t.message}")
            }
        })
    }

    // 3. Create Data (MENGGUNAKAN ENQUEUE)
    fun addCategory(context: Context, name: String, icon: String = "default") {
        if (name.isBlank()) {
            Toast.makeText(context, "Nama kategori wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        val call = categoryRepository.createCategory(name, currentTab, icon)

        call.enqueue(object : Callback<CategoryActionResponse> {
            override fun onResponse(
                call: Call<CategoryActionResponse>,
                response: Response<CategoryActionResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Kategori berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    loadCategories() // Refresh list otomatis
                } else {
                    Toast.makeText(context, "Gagal membuat kategori", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CategoryActionResponse>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 4. Update Data (MENGGUNAKAN ENQUEUE)
    fun updateCategory(context: Context, id: Int, name: String, icon: String = "default") {
        if (name.isBlank()) {
            Toast.makeText(context, "Nama kategori wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        val call = categoryRepository.updateCategory(id, name, currentTab, icon)

        call.enqueue(object : Callback<CategoryActionResponse> {
            override fun onResponse(
                call: Call<CategoryActionResponse>,
                response: Response<CategoryActionResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Berhasil diupdate!", Toast.LENGTH_SHORT).show()
                    loadCategories() // Refresh list
                } else {
                    Toast.makeText(context, "Gagal update", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CategoryActionResponse>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 5. Delete Data (MENGGUNAKAN ENQUEUE)
    fun deleteCategory(context: Context, categoryId: Int) {
        isLoading = true
        val call = categoryRepository.deleteCategory(categoryId)

        call.enqueue(object : Callback<CategoryActionResponse> {
            override fun onResponse(
                call: Call<CategoryActionResponse>,
                response: Response<CategoryActionResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Kategori dihapus", Toast.LENGTH_SHORT).show()
                    loadCategories() // Refresh list
                } else {
                    Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CategoryActionResponse>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- FACTORY ---
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as BubuApplication)
                CategoryViewModel(
                    categoryRepository = app.container.categoryRepository
                )
            }
        }
    }
}