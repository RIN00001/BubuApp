package com.example.todolistapp.repositories

import com.example.todolistapp.models.AttachWalletResponse
import com.example.todolistapp.models.BookCreateRequest
import com.example.todolistapp.models.DeleteBookResponse
import com.example.todolistapp.models.DetachWalletResponse
import com.example.todolistapp.models.GetAllBooksResponse
import com.example.todolistapp.models.GetBookResponse
import com.example.todolistapp.models.GetBookWalletsResponse
import com.example.todolistapp.models.PostBookResponse
import com.example.todolistapp.services.BookAPIService
import retrofit2.Call

interface BookRepositoryInterface {
    fun getAllBooks(): Call<GetAllBooksResponse>
    fun getBookById(bookId: Int): Call<GetBookResponse>
    fun createBook(request: BookCreateRequest): Call<PostBookResponse>
    fun updateBook(bookId: Int, request: BookCreateRequest): Call<PostBookResponse>
    fun deleteBook(bookId: Int): Call<DeleteBookResponse>

    // Wallet-Book relationship methods
    fun attachWallet(bookId: Int, walletId: Int): Call<AttachWalletResponse>
    fun detachWallet(bookId: Int, walletId: Int): Call<DetachWalletResponse>
    fun getBookWallets(bookId: Int): Call<GetBookWalletsResponse>
}

class BookRepository(
    private val bookAPIService: BookAPIService
) : BookRepositoryInterface {
    override fun getAllBooks(): Call<GetAllBooksResponse> = bookAPIService.getAllBooks()
    override fun getBookById(bookId: Int): Call<GetBookResponse> = bookAPIService.getBookById(bookId)
    override fun createBook(request: BookCreateRequest): Call<PostBookResponse> = bookAPIService.createBook(request)
    override fun updateBook(bookId: Int, request: BookCreateRequest): Call<PostBookResponse> = bookAPIService.updateBook(bookId, request)
    override fun deleteBook(bookId: Int): Call<DeleteBookResponse> = bookAPIService.deleteBook(bookId)
    override fun attachWallet(bookId: Int, walletId: Int): Call<AttachWalletResponse> = bookAPIService.attachWallet(bookId, walletId)
    override fun detachWallet(bookId: Int, walletId: Int): Call<DetachWalletResponse> = bookAPIService.detachWallet(bookId, walletId)
    override fun getBookWallets(bookId: Int): Call<GetBookWalletsResponse> = bookAPIService.getBookWallets(bookId)
}
