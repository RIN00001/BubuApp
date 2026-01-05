package com.example.todolistapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.models.BookCreateRequest
import com.example.todolistapp.models.ErrorModel
import com.example.todolistapp.models.AttachWalletResponse
import com.example.todolistapp.models.DetachWalletResponse
import com.example.todolistapp.repositories.BookRepository
import com.example.todolistapp.repositories.BookRepositoryInterface
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.uiStates.BookListStatusUIState
import com.example.todolistapp.uiStates.BookDetailStatusUIState
import com.example.todolistapp.uiStates.BookMutationStatusUIState
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okio.IOException


class BookViewModel(
    private val bookRepository: BookRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {
    // LIST STATE
    private val _listState = MutableStateFlow<BookListStatusUIState>(BookListStatusUIState.Start)
    val listState: StateFlow<BookListStatusUIState> = _listState.asStateFlow()
    // DETAIL STATE
    private val _detailState = MutableStateFlow<BookDetailStatusUIState>(BookDetailStatusUIState.Start)
    val detailState: StateFlow<BookDetailStatusUIState> = _detailState.asStateFlow()
    // M STATE (create/update/delete)
    private val _mutationState = MutableStateFlow<BookMutationStatusUIState>(BookMutationStatusUIState.Start)
    val mutationState: StateFlow<BookMutationStatusUIState> = _mutationState.asStateFlow()
    fun fetchBooks() {
        viewModelScope.launch {
            _listState.value = BookListStatusUIState.Loading

            try {
                val call = bookRepository.getAllBooks()

                call.enqueue(object : Callback<com.example.todolistapp.models.GetAllBooksResponse> {
                    override fun onResponse(
                        call: Call<com.example.todolistapp.models.GetAllBooksResponse>,
                        res: Response<com.example.todolistapp.models.GetAllBooksResponse>
                    ) {
                        if (res.isSuccessful) {
                            val books = res.body()?.data ?: emptyList()
                            _listState.value = BookListStatusUIState.Success(books)
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )
                            _listState.value = BookListStatusUIState.Failed(errorMessage.errors)
                        }
                    }

                    override fun onFailure(call: Call<com.example.todolistapp.models.GetAllBooksResponse>, t: Throwable) {
                        _listState.value = BookListStatusUIState.Failed(t.localizedMessage)
                    }
                })

            } catch (ex: IOException) {
                _listState.value = BookListStatusUIState.Failed(ex.localizedMessage)
            }
        }
    }

    fun fetchBookDetail(bookId: Int) {
        viewModelScope.launch {
            _detailState.value = BookDetailStatusUIState.Loading

            try {
                val call = bookRepository.getBookById(bookId)

                call.enqueue(object : Callback<com.example.todolistapp.models.GetBookResponse> {
                    override fun onResponse(
                        call: Call<com.example.todolistapp.models.GetBookResponse>,
                        res: Response<com.example.todolistapp.models.GetBookResponse>
                    ) {
                        if (res.isSuccessful) {
                            _detailState.value = BookDetailStatusUIState.Success(res.body()!!.data)
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )
                            _detailState.value = BookDetailStatusUIState.Failed(errorMessage.errors)
                        }
                    }

                    override fun onFailure(call: Call<com.example.todolistapp.models.GetBookResponse>, t: Throwable) {
                        _detailState.value = BookDetailStatusUIState.Failed(t.localizedMessage)
                    }
                })

            } catch (ex: IOException) {
                _detailState.value = BookDetailStatusUIState.Failed(ex.localizedMessage)
            }
        }
    }

    // -----------------------------
    // Create Book
    // -----------------------------
    fun createBook(name: String, program: String?, walletIds: List<Int>?) {
        viewModelScope.launch {
            _mutationState.value = BookMutationStatusUIState.Loading

            try {
                val req = BookCreateRequest(name, program, walletIds)
                val call = bookRepository.createBook(req)

                call.enqueue(object : Callback<com.example.todolistapp.models.PostBookResponse> {
                    override fun onResponse(
                        call: Call<com.example.todolistapp.models.PostBookResponse>,
                        res: Response<com.example.todolistapp.models.PostBookResponse>
                    ) {
                        if (res.isSuccessful) {
                            _mutationState.value = BookMutationStatusUIState.Success("Book created")
                            fetchBooks()
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )
                            _mutationState.value = BookMutationStatusUIState.Failed(errorMessage.errors)
                        }
                    }

                    override fun onFailure(call: Call<com.example.todolistapp.models.PostBookResponse>, t: Throwable) {
                        _mutationState.value = BookMutationStatusUIState.Failed(t.localizedMessage)
                    }
                })

            } catch (ex: IOException) {
                _mutationState.value = BookMutationStatusUIState.Failed(ex.localizedMessage)
            }
        }
    }


    // Update Book

    fun updateBook(bookId: Int, name: String, program: String?, walletIds: List<Int>?) {
        viewModelScope.launch {
            _mutationState.value = BookMutationStatusUIState.Loading

            try {
                val req = BookCreateRequest(name, program, walletIds)
                val call = bookRepository.updateBook(bookId, req)

                call.enqueue(object : Callback<com.example.todolistapp.models.PostBookResponse> {
                    override fun onResponse(
                        call: Call<com.example.todolistapp.models.PostBookResponse>,
                        res: Response<com.example.todolistapp.models.PostBookResponse>
                    ) {
                        if (res.isSuccessful) {
                            _mutationState.value = BookMutationStatusUIState.Success("Book updated")
                            fetchBooks()
                        } else {
                            val error = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                            _mutationState.value = BookMutationStatusUIState.Failed(error.errors)
                        }
                    }

                    override fun onFailure(call: Call<com.example.todolistapp.models.PostBookResponse>, t: Throwable) {
                        _mutationState.value = BookMutationStatusUIState.Failed(t.localizedMessage)
                    }
                })

            } catch (ex: IOException) {
                _mutationState.value = BookMutationStatusUIState.Failed(ex.localizedMessage)
            }
        }
    }

    // -----------------------------
    // Delete Book
    // -----------------------------
    fun deleteBook(bookId: Int) {
        viewModelScope.launch {
            _mutationState.value = BookMutationStatusUIState.Loading

            try {
                val call = bookRepository.deleteBook(bookId)

                call.enqueue(object : Callback<com.example.todolistapp.models.DeleteBookResponse> {
                    override fun onResponse(
                        call: Call<com.example.todolistapp.models.DeleteBookResponse>,
                        res: Response<com.example.todolistapp.models.DeleteBookResponse>
                    ) {
                        if (res.isSuccessful) {
                            _mutationState.value = BookMutationStatusUIState.Success("Book deleted")
                            fetchBooks()
                        } else {
                            val error = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                            _mutationState.value = BookMutationStatusUIState.Failed(error.errors)
                        }
                    }

                    override fun onFailure(call: Call<com.example.todolistapp.models.DeleteBookResponse>, t: Throwable) {
                        _mutationState.value = BookMutationStatusUIState.Failed(t.localizedMessage)
                    }
                })

            } catch (ex: IOException) {
                _mutationState.value = BookMutationStatusUIState.Failed(ex.localizedMessage)
            }
        }
    }

    // -----------------------------
    // Attach Wallet to Book
    // -----------------------------
    fun attachWallet(bookId: Int, walletId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val call = bookRepository.attachWallet(bookId, walletId)

                call.enqueue(object : Callback<AttachWalletResponse> {
                    override fun onResponse(
                        call: Call<AttachWalletResponse>,
                        res: Response<AttachWalletResponse>
                    ) {
                        if (res.isSuccessful) {
                            onSuccess()
                        }
                    }

                    override fun onFailure(call: Call<AttachWalletResponse>, t: Throwable) {
                        // Handle failure silently or log
                    }
                })

            } catch (_: IOException) {
                // Handle exception
            }
        }
    }

    // -----------------------------
    // Detach Wallet from Book
    // -----------------------------
    fun detachWallet(bookId: Int, walletId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val call = bookRepository.detachWallet(bookId, walletId)

                call.enqueue(object : Callback<DetachWalletResponse> {
                    override fun onResponse(
                        call: Call<DetachWalletResponse>,
                        res: Response<DetachWalletResponse>
                    ) {
                        if (res.isSuccessful) {
                            onSuccess()
                        }
                    }

                    override fun onFailure(call: Call<DetachWalletResponse>, t: Throwable) {
                        // Handle failure silently or log
                    }
                })

            } catch (_: IOException) {
                // Handle exception
            }
        }
    }


    // Resetters (for Compose UI later)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as BubuApplication)

                val bookRepository = application.container.bookRepository
                val userRepository = application.container.userRepository

                BookViewModel(
                    bookRepository = bookRepository,
                    userRepository = userRepository
                )
            }
        }
    }

    fun resetListState() { _listState.value = BookListStatusUIState.Start }
    fun resetDetailState() { _detailState.value = BookDetailStatusUIState.Start }
    fun resetMutationState() { _mutationState.value = BookMutationStatusUIState.Start }
}
