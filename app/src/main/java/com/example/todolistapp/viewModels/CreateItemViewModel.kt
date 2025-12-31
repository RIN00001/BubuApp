package com.example.todolistapp.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.todolistapp.TodoListApplication
import com.example.todolistapp.models.CategoryModel
import com.example.todolistapp.models.ItemCreateRequest
import com.example.todolistapp.models.PostItemResponse
import com.example.todolistapp.repositories.CategoryRepositoryInterface
import com.example.todolistapp.repositories.ItemRepositoryInterface
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateItemViewModel(
    private val itemRepository: ItemRepositoryInterface,
    private val categoryRepository: CategoryRepositoryInterface
) : ViewModel() {


    var name by mutableStateOf("")
    var amountString by mutableStateOf("")
    var type by mutableStateOf("EXPENSE")

    var date by mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))


    var categoryId by mutableIntStateOf(0)
    var categories = mutableStateListOf<CategoryModel>()

    var isLoading by mutableStateOf(false)
    var currentItemId by mutableStateOf<Int?>(null)

    // --- LOAD KATEGORI (Coroutine) ---
    fun loadCategories() {
        viewModelScope.launch {
            try {
                // Panggil repository (suspend function)
                val response = categoryRepository.getAllCategories(type)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        categories.clear()
                        // Pastikan response body structure sesuai (body.data)
                        categories.addAll(body.data)
                    }
                } else {
                    Log.e("CreateItemVM", "Gagal load kategori: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CreateItemVM", "Error connection category", e)
            }
        }
    }

    // --- SETUP SAAT EDIT ---
    // Pastikan urutan parameter sesuai dengan panggilan di View
    fun setupEditMode(id: Int, existingName: String, existingAmount: Long, existingType: String, existingDate: String, existingCatId: Int) {
        currentItemId = id
        name = existingName
        amountString = existingAmount.toString()
        type = existingType
        // Jika tanggal kosong dari API, pakai tanggal hari ini
        date = if (existingDate.isNotBlank()) existingDate else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        categoryId = existingCatId
    }

    // --- SIMPAN TRANSAKSI (Retrofit Call) ---
    fun saveTransaction(navController: NavHostController, context: Context) {
        val amount = amountString.toLongOrNull() // Konversi ke Long

        // Validasi
        if (name.isBlank() || amount == null) {
            Toast.makeText(context, "Nama dan Jumlah harus diisi", Toast.LENGTH_SHORT).show()
            return
        }
        if (categoryId == 0) {
            Toast.makeText(context, "Pilih kategori terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true

        val request = ItemCreateRequest(
            name = name,
            amount = amount,
            type = type,
            categoryId = categoryId,
            date = date,
            bookId = 1,
            walletId = 1
        )


        val callback = object : Callback<PostItemResponse> {
            override fun onResponse(call: Call<PostItemResponse>, response: Response<PostItemResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val msg = if (currentItemId != null) "Berhasil update!" else "Berhasil simpan!"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Gagal: ${response.code()} ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("CreateItemVM", "Response Err: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PostItemResponse>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "Koneksi Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("CreateItemVM", "Failure", t)
            }
        }

        if (currentItemId != null) {
            itemRepository.updateItem(currentItemId!!, request).enqueue(callback)
        } else {
            itemRepository.createItem(request).enqueue(callback)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoListApplication)
                val container = application.container

                CreateItemViewModel(
                    itemRepository = container.itemRepository,
                    categoryRepository = container.categoryRepository
                )
            }
        }
    }
}