package com.example.todolistapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.models.*
import com.example.todolistapp.repositories.CategoryRepositoryInterface
import com.example.todolistapp.repositories.ItemRepositoryInterface
import com.example.todolistapp.repositories.WalletRepositoryInterface
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
    private val itemRepository: ItemRepositoryInterface,
    private val walletRepository: WalletRepositoryInterface,
    private val categoryRepository: CategoryRepositoryInterface
) : ViewModel() {

    // --- STATES ---
    private val _listState = MutableStateFlow<ItemListStatusUIState>(ItemListStatusUIState.Start)
    val listState: StateFlow<ItemListStatusUIState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<ItemDetailStatusUIState>(ItemDetailStatusUIState.Start)
    val detailState: StateFlow<ItemDetailStatusUIState> = _detailState.asStateFlow()

    private val _mutationState = MutableStateFlow<ItemMutationStatusUIState>(ItemMutationStatusUIState.Start)
    val mutationState: StateFlow<ItemMutationStatusUIState> = _mutationState.asStateFlow()

    private val _walletDropdownState = MutableStateFlow<List<WalletModel>>(emptyList())
    val walletDropdownState: StateFlow<List<WalletModel>> = _walletDropdownState.asStateFlow()

    private val _categoryDropdownState = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categoryDropdownState: StateFlow<List<CategoryModel>> = _categoryDropdownState.asStateFlow()


    // --- FUNGSI GET (Menggunakan Enqueue) ---

    fun fetchItemsByBook(bookId: Int) {
        viewModelScope.launch {
            _listState.value = ItemListStatusUIState.Loading
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayDate = dateFormat.format(Date())

                // Pastikan itemRepository.getItemsByBook mengembalikan Call<...>
                val call = itemRepository.getItemsByBook(bookId, todayDate)

                call.enqueue(object : Callback<GetAllItemsResponse> {
                    override fun onResponse(call: Call<GetAllItemsResponse>, res: Response<GetAllItemsResponse>) {
                        if (res.isSuccessful) {
                            val items = res.body()?.data ?: emptyList()
                            _listState.value = ItemListStatusUIState.Success(items)
                        } else {
                            _listState.value = ItemListStatusUIState.Failed("Gagal memuat data")
                        }
                    }
                    override fun onFailure(call: Call<GetAllItemsResponse>, t: Throwable) {
                        _listState.value = ItemListStatusUIState.Failed(t.localizedMessage ?: "Connection error")
                    }
                })
            } catch (ex: Exception) {
                _listState.value = ItemListStatusUIState.Failed(ex.localizedMessage ?: "Error")
            }
        }
    }

    // Mengambil Wallet (Dropdown) - MENGGUNAKAN ENQUEUE
    fun fetchWalletsForDropdown() {
        viewModelScope.launch {
            try {
                val call = walletRepository.getAllWallets() // Harus return Call
                call.enqueue(object : Callback<GetAllWalletsResponse> {
                    override fun onResponse(call: Call<GetAllWalletsResponse>, res: Response<GetAllWalletsResponse>) {
                        if (res.isSuccessful) {
                            _walletDropdownState.value = res.body()?.data ?: emptyList()
                        }
                    }
                    override fun onFailure(call: Call<GetAllWalletsResponse>, t: Throwable) {
                        // Silent fail agar UI tidak crash
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Mengambil Category (Dropdown) - MENGGUNAKAN ENQUEUE
    fun fetchCategoriesForDropdown() {
        viewModelScope.launch {
            try {
                // Sekarang ini VALID karena Repository sudah diganti return Call
                val call = categoryRepository.getAllCategories()

                call.enqueue(object : Callback<GetAllCategoriesResponse> {
                    override fun onResponse(call: Call<GetAllCategoriesResponse>, res: Response<GetAllCategoriesResponse>) {
                        if (res.isSuccessful) {
                            _categoryDropdownState.value = res.body()?.data ?: emptyList()
                        }
                    }
                    override fun onFailure(call: Call<GetAllCategoriesResponse>, t: Throwable) {
                        // Silent fail
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- FUNGSI WRITE (Create/Update/Delete) ---

    fun createItem(name: String, amount: Double, type: String, bookId: Int, walletId: Int?, categoryId: Int?) {
        viewModelScope.launch {
            _mutationState.value = ItemMutationStatusUIState.Loading
            try {
                val req = ItemCreateRequest(name, amount, type, bookId, walletId, categoryId)
                val call = itemRepository.createItem(req)

                call.enqueue(object : Callback<PostItemResponse> {
                    override fun onResponse(call: Call<PostItemResponse>, res: Response<PostItemResponse>) {
                        if (res.isSuccessful) {
                            _mutationState.value = ItemMutationStatusUIState.Success("Berhasil disimpan")
                            fetchItemsByBook(bookId)
                            fetchWalletsForDropdown()
                        } else {
                            _mutationState.value = ItemMutationStatusUIState.Failed("Gagal menyimpan")
                        }
                    }
                    override fun onFailure(call: Call<PostItemResponse>, t: Throwable) {
                        _mutationState.value = ItemMutationStatusUIState.Failed("Error koneksi")
                    }
                })
            } catch (ex: Exception) {
                _mutationState.value = ItemMutationStatusUIState.Failed(ex.localizedMessage ?: "Error")
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
                            _mutationState.value = ItemMutationStatusUIState.Success("Berhasil diupdate")
                            fetchItemsByBook(bookId)
                            fetchWalletsForDropdown()
                        } else {
                            _mutationState.value = ItemMutationStatusUIState.Failed("Gagal update")
                        }
                    }
                    override fun onFailure(call: Call<PostItemResponse>, t: Throwable) {
                        _mutationState.value = ItemMutationStatusUIState.Failed("Error koneksi")
                    }
                })
            } catch (ex: Exception) {
                _mutationState.value = ItemMutationStatusUIState.Failed(ex.localizedMessage ?: "Error")
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
                            _mutationState.value = ItemMutationStatusUIState.Success("Berhasil dihapus")
                            fetchItemsByBook(bookId)
                        } else {
                            _mutationState.value = ItemMutationStatusUIState.Failed("Gagal hapus")
                        }
                    }
                    override fun onFailure(call: Call<DeleteItemResponse>, t: Throwable) {
                        _mutationState.value = ItemMutationStatusUIState.Failed("Error koneksi")
                    }
                })
            } catch (ex: Exception) {
                _mutationState.value = ItemMutationStatusUIState.Failed(ex.localizedMessage ?: "Error")
            }
        }
    }

    fun resetMutationState() {
        _mutationState.value = ItemMutationStatusUIState.Start
    }

    // --- FACTORY ---
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BubuApplication)
                ItemViewModel(
                    itemRepository = application.container.itemRepository,
                    walletRepository = application.container.walletRepository,
                    categoryRepository = application.container.categoryRepository
                )
            }
        }
    }
}