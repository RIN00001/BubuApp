package com.example.todolistapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.models.*
import com.example.todolistapp.repositories.ItemRepositoryInterface
import com.example.todolistapp.uiStates.ItemDetailStatusUIState
import com.example.todolistapp.uiStates.ItemListStatusUIState
import com.example.todolistapp.uiStates.ItemMutationStatusUIState
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemViewModel(
    private val itemRepository: ItemRepositoryInterface
) : ViewModel() {

    // ----------------------------------------------------------------
    // 1. STATE MANAGEMENT
    // ----------------------------------------------------------------

    // State untuk List Data (Dashboard)
    private val _listState = MutableStateFlow<ItemListStatusUIState>(ItemListStatusUIState.Start)
    val listState: StateFlow<ItemListStatusUIState> = _listState.asStateFlow()

    // State untuk Detail Data (Form Edit)
    private val _detailState = MutableStateFlow<ItemDetailStatusUIState>(ItemDetailStatusUIState.Start)
    val detailState: StateFlow<ItemDetailStatusUIState> = _detailState.asStateFlow()

    // State untuk Proses Loading/Success/Error saat Create/Update/Delete
    private val _mutationState = MutableStateFlow<ItemMutationStatusUIState>(ItemMutationStatusUIState.Start)
    val mutationState: StateFlow<ItemMutationStatusUIState> = _mutationState.asStateFlow()


    // ----------------------------------------------------------------
    // 2. READ OPERATIONS (GET)
    // ----------------------------------------------------------------

    // Fungsi Utama: Mengambil data transaksi per buku
    fun fetchItemsByBook(bookId: Int) {
        viewModelScope.launch {
            _listState.value = ItemListStatusUIState.Loading
            try {
                // --- LOGIC BARU: GENERATE TANGGAL HARI INI ---
                // Backend mewajibkan parameter date. Kita kirim tanggal hari ini "yyyy-MM-dd".
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayDate = dateFormat.format(Date())

                val call = itemRepository.getItemsByBook(bookId, todayDate)

                call.enqueue(object : Callback<GetAllItemsResponse> {
                    override fun onResponse(call: Call<GetAllItemsResponse>, res: Response<GetAllItemsResponse>) {
                        if (res.isSuccessful) {
                            val items = res.body()?.data ?: emptyList()
                            _listState.value = ItemListStatusUIState.Success(items)
                        } else {
                            // Parsing Error dari Backend
                            val errorMsg = try {
                                val errorObj = Gson().fromJson(res.errorBody()?.charStream(), ErrorModel::class.java)
                                errorObj?.errors ?: "Gagal memuat data"
                            } catch (e: Exception) {
                                res.message()
                            }
                            _listState.value = ItemListStatusUIState.Failed(errorMsg)
                        }
                    }
                    override fun onFailure(call: Call<GetAllItemsResponse>, t: Throwable) {
                        _listState.value = ItemListStatusUIState.Failed(t.localizedMessage ?: "Connection error")
                    }
                })
            } catch (ex: IOException) {
                _listState.value = ItemListStatusUIState.Failed(ex.localizedMessage ?: "IO Exception")
            }
        }
    }

    // Fungsi: Mengambil detail 1 item (dipanggil saat mau edit)
    fun fetchItemDetail(itemId: Int) {
        viewModelScope.launch {
            _detailState.value = ItemDetailStatusUIState.Loading
            try {
                val call = itemRepository.getItemById(itemId)
                call.enqueue(object : Callback<GetItemResponse> {
                    override fun onResponse(call: Call<GetItemResponse>, res: Response<GetItemResponse>) {
                        if (res.isSuccessful) {
                            _detailState.value = ItemDetailStatusUIState.Success(res.body()!!.data)
                        } else {
                            _detailState.value = ItemDetailStatusUIState.Failed("Gagal mengambil detail")
                        }
                    }
                    override fun onFailure(call: Call<GetItemResponse>, t: Throwable) {
                        _detailState.value = ItemDetailStatusUIState.Failed(t.localizedMessage ?: "Error")
                    }
                })
            } catch (ex: IOException) {
                _detailState.value = ItemDetailStatusUIState.Failed(ex.localizedMessage ?: "IO Error")
            }
        }
    }


    // ----------------------------------------------------------------
    // 3. WRITE OPERATIONS (CREATE, UPDATE, DELETE)
    // ----------------------------------------------------------------

    fun createItem(name: String, amount: Double, type: String, bookId: Int, walletId: Int?, categoryId: Int?) {
        viewModelScope.launch {
            _mutationState.value = ItemMutationStatusUIState.Loading
            try {
                // Membuat Request Body
                val req = ItemCreateRequest(name, amount, type, bookId, walletId, categoryId)
                val call = itemRepository.createItem(req)

                call.enqueue(object : Callback<PostItemResponse> {
                    override fun onResponse(call: Call<PostItemResponse>, res: Response<PostItemResponse>) {
                        if (res.isSuccessful) {
                            _mutationState.value = ItemMutationStatusUIState.Success("Transaksi berhasil disimpan")
                            // Refresh List (Agar data baru muncul)
                            fetchItemsByBook(bookId)
                        } else {
                            val errorMsg = try {
                                val errorObj = Gson().fromJson(res.errorBody()?.charStream(), ErrorModel::class.java)
                                errorObj?.errors ?: "Gagal menyimpan"
                            } catch (e: Exception) { "Error server" }
                            _mutationState.value = ItemMutationStatusUIState.Failed(errorMsg)
                        }
                    }
                    override fun onFailure(call: Call<PostItemResponse>, t: Throwable) {
                        _mutationState.value = ItemMutationStatusUIState.Failed(t.localizedMessage ?: "Connection error")
                    }
                })
            } catch (ex: IOException) {
                _mutationState.value = ItemMutationStatusUIState.Failed(ex.localizedMessage ?: "IO Exception")
            }
        }
    }

    fun updateItem(itemId: Int, name: String, amount: Double, type: String, bookId: Int, walletId: Int?, categoryId: Int?) {
        viewModelScope.launch {
            _mutationState.value = ItemMutationStatusUIState.Loading
            try {
                val req = ItemCreateRequest(name, amount, type, bookId, walletId, categoryId)
                val call = itemRepository.updateItem(itemId, req)

                call.enqueue(object : Callback<PostItemResponse> {
                    override fun onResponse(call: Call<PostItemResponse>, res: Response<PostItemResponse>) {
                        if (res.isSuccessful) {
                            _mutationState.value = ItemMutationStatusUIState.Success("Transaksi berhasil diupdate")
                            // Refresh List
                            fetchItemsByBook(bookId)
                        } else {
                            _mutationState.value = ItemMutationStatusUIState.Failed("Gagal update data")
                        }
                    }
                    override fun onFailure(call: Call<PostItemResponse>, t: Throwable) {
                        _mutationState.value = ItemMutationStatusUIState.Failed(t.localizedMessage ?: "Connection error")
                    }
                })
            } catch (ex: IOException) {
                _mutationState.value = ItemMutationStatusUIState.Failed(ex.localizedMessage ?: "IO Exception")
            }
        }
    }

    fun deleteItem(itemId: Int, bookId: Int) {
        viewModelScope.launch {
            _mutationState.value = ItemMutationStatusUIState.Loading
            try {
                val call = itemRepository.deleteItem(itemId)
                call.enqueue(object : Callback<DeleteItemResponse> {
                    override fun onResponse(call: Call<DeleteItemResponse>, res: Response<DeleteItemResponse>) {
                        if (res.isSuccessful) {
                            _mutationState.value = ItemMutationStatusUIState.Success("Transaksi berhasil dihapus")
                            // Refresh List
                            fetchItemsByBook(bookId)
                        } else {
                            _mutationState.value = ItemMutationStatusUIState.Failed("Gagal menghapus data")
                        }
                    }
                    override fun onFailure(call: Call<DeleteItemResponse>, t: Throwable) {
                        _mutationState.value = ItemMutationStatusUIState.Failed(t.localizedMessage ?: "Connection error")
                    }
                })
            } catch (ex: IOException) {
                _mutationState.value = ItemMutationStatusUIState.Failed(ex.localizedMessage ?: "IO Exception")
            }
        }
    }

    fun resetMutationState() {
        _mutationState.value = ItemMutationStatusUIState.Success("")
    }

    // ----------------------------------------------------------------
    // 4. FACTORY (Menggunakan BubuApplication)
    // ----------------------------------------------------------------
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // CAST KE BubuApplication (Sesuai kode kamu)
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BubuApplication)
                val itemRepository = application.container.itemRepository
                ItemViewModel(itemRepository = itemRepository)
            }
        }
    }
}