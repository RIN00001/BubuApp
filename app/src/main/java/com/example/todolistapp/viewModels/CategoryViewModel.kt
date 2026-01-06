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
    var categories by mutableStateOf<List<CategoryModel>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentTab by mutableStateOf("EXPENSE")
        private set

    // --- DAFTAR DEFAULT (Nama, IconKey) ---
    private val defaultExpenses = listOf(
        Pair("Makanan & Minuman", "food"),
        Pair("Transportasi", "transport"),
        Pair("Belanja", "shopping"),
        Pair("Tagihan", "bill"),
        Pair("Hiburan", "entertainment"),
        Pair("Kesehatan", "health"),
        Pair("Pendidikan", "education"),
        Pair("Cicilan", "installment"),
        Pair("Lain-lain", "other")
    )

    private val defaultIncomes = listOf(
        Pair("Gaji", "salary"),
        Pair("Bonus", "bonus"),
        Pair("Investasi", "investment")
    )

    init {
        loadCategories()
    }

    // 1. Ganti Tab
    fun onTabSelected(type: String) {
        currentTab = type
        loadCategories()
    }

    // 2. Load Data (+ Logic Auto Seed)
    fun loadCategories() {
        isLoading = true

        val call = categoryRepository.getAllCategories(currentTab)

        call.enqueue(object : Callback<GetAllCategoriesResponse> {
            override fun onResponse(
                call: Call<GetAllCategoriesResponse>,
                response: Response<GetAllCategoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val allData = response.body()?.data ?: emptyList()
                    val filteredData = allData.filter { it.type == currentTab }

                    categories = filteredData

                    // JIKA DATA KOSONG -> JALANKAN SEEDING
                    if (filteredData.isEmpty()) {
                        Log.d("CAT_VM", "Data kosong untuk $currentTab, mulai seeding...")
                        seedCategories(currentTab)
                    } else {
                        isLoading = false
                    }
                } else {
                    isLoading = false
                    Log.e("CAT_VM", "Gagal load: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GetAllCategoriesResponse>, t: Throwable) {
                isLoading = false
                Log.e("CAT_VM", "Error connection: ${t.message}")
            }
        })
    }

    // --- LOGIC AUTO SEED ---
    private fun seedCategories(type: String) {
        val listToSeed = if (type == "EXPENSE") defaultExpenses else defaultIncomes
        var successCount = 0
        var completedCount = 0

        listToSeed.forEach { (name, iconKey) ->
            // Kirim iconKey ke API
            val call = categoryRepository.createCategory(name, type, iconKey)

            call.enqueue(object : Callback<CategoryActionResponse> {
                override fun onResponse(call: Call<CategoryActionResponse>, response: Response<CategoryActionResponse>) {
                    completedCount++
                    if (response.isSuccessful) successCount++
                    checkSeedingComplete(completedCount, listToSeed.size)
                }

                override fun onFailure(call: Call<CategoryActionResponse>, t: Throwable) {
                    completedCount++
                    checkSeedingComplete(completedCount, listToSeed.size)
                }
            })
        }
    }

    private fun checkSeedingComplete(completed: Int, total: Int) {
        if (completed == total) {
            // Setelah seeding selesai, load ulang agar muncul di list
            reloadAfterSeed()
        }
    }

    private fun reloadAfterSeed() {
        val call = categoryRepository.getAllCategories(currentTab)
        call.enqueue(object : Callback<GetAllCategoriesResponse> {
            override fun onResponse(call: Call<GetAllCategoriesResponse>, response: Response<GetAllCategoriesResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val allData = response.body()?.data ?: emptyList()
                    categories = allData.filter { it.type == currentTab }
                }
            }
            override fun onFailure(call: Call<GetAllCategoriesResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    // 3. Create Manual (Dari User)
    fun addCategory(context: Context, name: String) {
        if (name.isBlank()) {
            Toast.makeText(context, "Nama kategori wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }
        isLoading = true
        // Default icon untuk buatan user manual adalah 'other'
        val call = categoryRepository.createCategory(name, currentTab, "other")

        call.enqueue(object : Callback<CategoryActionResponse> {
            override fun onResponse(call: Call<CategoryActionResponse>, response: Response<CategoryActionResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Kategori dibuat", Toast.LENGTH_SHORT).show()
                    loadCategories()
                } else {
                    Toast.makeText(context, "Gagal membuat", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CategoryActionResponse>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 4. Update
    fun updateCategory(context: Context, id: Int, name: String, icon: String) {
        if (name.isBlank()) return
        isLoading = true
        val call = categoryRepository.updateCategory(id, name, currentTab, icon)

        call.enqueue(object : Callback<CategoryActionResponse> {
            override fun onResponse(call: Call<CategoryActionResponse>, response: Response<CategoryActionResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Berhasil update", Toast.LENGTH_SHORT).show()
                    loadCategories()
                }
            }
            override fun onFailure(call: Call<CategoryActionResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    // 5. Delete
    fun deleteCategory(context: Context, categoryId: Int) {
        isLoading = true
        val call = categoryRepository.deleteCategory(categoryId)

        call.enqueue(object : Callback<CategoryActionResponse> {
            override fun onResponse(call: Call<CategoryActionResponse>, response: Response<CategoryActionResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    Toast.makeText(context, "Terhapus", Toast.LENGTH_SHORT).show()
                    loadCategories()
                }
            }
            override fun onFailure(call: Call<CategoryActionResponse>, t: Throwable) {
                isLoading = false
            }
        })
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as BubuApplication)
                CategoryViewModel(app.container.categoryRepository)
            }
        }
    }
}