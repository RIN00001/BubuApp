package com.example.todolistapp.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.models.DeleteItemResponse
import com.example.todolistapp.models.GetAllItemsResponse
import com.example.todolistapp.repositories.ItemRepository
import com.example.todolistapp.uiStates.HomeUIState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    var homeUIState: HomeUIState by mutableStateOf(HomeUIState.Loading)
        private set

    init {
        getAllItems()
    }

    // --- GAYA KURIKULUM: MENGGUNAKAN ENQUEUE (CALLBACK) ---

    fun getAllItems() {
        homeUIState = HomeUIState.Loading

        // 1. Panggil Repository (dapat Call)
        val call = itemRepository.getAllItems()

        // 2. Gunakan Enqueue
        call.enqueue(object : Callback<GetAllItemsResponse> {
            override fun onResponse(
                call: Call<GetAllItemsResponse>,
                response: Response<GetAllItemsResponse>
            ) {
                if (response.isSuccessful) {
                    // Ambil body, lalu ambil data di dalamnya
                    val listData = response.body()?.data

                    if (listData != null) {
                        homeUIState = HomeUIState.Success(listData)
                    } else {
                        // Sukses tapi list kosong (atau null)
                        homeUIState = HomeUIState.Error
                    }
                } else {
                    // Error dari server (404, 500, dll)
                    Log.e("HomeViewModel", "Error Code: ${response.code()}")
                    homeUIState = HomeUIState.Error
                }
            }

            override fun onFailure(call: Call<GetAllItemsResponse>, t: Throwable) {
                // Error jaringan / koneksi
                Log.e("HomeViewModel", "Network Error: ${t.message}")
                homeUIState = HomeUIState.Error
            }
        })
    }

    fun deleteItem(itemId: Int) {
        // Hati-hati: Delete biasanya tidak mengubah state loading utama agar layar tidak berkedip,
        // tapi tergantung kebutuhan UI kamu. Di sini kita langsung panggil.

        val call = itemRepository.deleteItem(itemId)

        call.enqueue(object : Callback<DeleteItemResponse> {
            override fun onResponse(
                call: Call<DeleteItemResponse>,
                response: Response<DeleteItemResponse>
            ) {
                if (response.isSuccessful) {
                    // Jika delete berhasil, refresh data
                    Log.d("HomeViewModel", "Delete Success")
                    getAllItems()
                } else {
                    Log.e("HomeViewModel", "Delete Failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DeleteItemResponse>, t: Throwable) {
                Log.e("HomeViewModel", "Delete Network Error: ${t.message}")
            }
        })
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BubuApplication)
                val itemRepository = application.container.itemRepository
                HomeViewModel(itemRepository = itemRepository)
            }
        }
    }
}