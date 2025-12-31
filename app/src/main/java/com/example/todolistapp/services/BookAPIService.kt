package com.example.todolistapp.services

import com.example.todolistapp.models.BookCreateRequest
import com.example.todolistapp.models.GetAllBooksResponse
import com.example.todolistapp.models.GetBookResponse
import com.example.todolistapp.models.PostBookResponse
import com.example.todolistapp.models.DeleteBookResponse
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
}