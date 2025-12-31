package com.example.todolistapp.services

import com.example.todolistapp.models.*
import retrofit2.Call
import retrofit2.http.*

interface ItemAPIService {


    @GET("api/items")
    fun getAllItems(): Call<GetAllItemsResponse>

    @GET("api/items")
    fun getItemsByBook(@Query("bookId") bookId: Int): Call<GetAllItemsResponse>

    @GET("api/items/{id}")
    fun getItemById(@Path("id") itemId: Int): Call<PostItemResponse>

    @POST("api/items")
    fun createItem(@Body request: ItemCreateRequest): Call<PostItemResponse>

    @PUT("api/items/{id}")
    fun updateItem(
        @Path("id") itemId: Int,
        @Body request: ItemCreateRequest
    ): Call<PostItemResponse>

    @DELETE("api/items/{id}")
    fun deleteItem(@Path("id") itemId: Int): Call<DeleteItemResponse>

}