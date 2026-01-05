package com.example.todolistapp.services

import com.example.todolistapp.models.AttachWalletResponse
import com.example.todolistapp.models.BookCreateRequest
import com.example.todolistapp.models.GetAllBooksResponse
import com.example.todolistapp.models.GetBookResponse
import com.example.todolistapp.models.PostBookResponse
import com.example.todolistapp.models.DeleteBookResponse
import com.example.todolistapp.models.DetachWalletResponse
import com.example.todolistapp.models.GetBookWalletsResponse
import retrofit2.Call
import retrofit2.http.*

interface BookAPIService{
    @GET("api/books")
    fun getAllBooks(): Call<GetAllBooksResponse>

    @GET("api/books/{id}")
    fun getBookById(@Path("id") bookId: Int): Call<GetBookResponse>

    @POST("api/books")
    fun createBook(@Body request: BookCreateRequest): Call<PostBookResponse>

    @PUT("api/books/{id}")
    fun updateBook(@Path("id") bookId: Int, @Body request: BookCreateRequest): Call<PostBookResponse>

    @DELETE("api/books/{id}")
    fun deleteBook(@Path("id") bookId: Int): Call<DeleteBookResponse>

    @POST("api/books/{id}/wallets/{walletId}")
    fun attachWallet(@Path("id") bookId: Int, @Path("walletId") walletId: Int): Call<AttachWalletResponse>

    @DELETE("api/books/{id}/wallets/{walletId}")
    fun detachWallet(@Path("id") bookId: Int, @Path("walletId") walletId: Int): Call<DetachWalletResponse>

    @GET("api/books/{id}/wallets")
    fun getBookWallets(@Path("id") bookId: Int): Call<GetBookWalletsResponse>
}