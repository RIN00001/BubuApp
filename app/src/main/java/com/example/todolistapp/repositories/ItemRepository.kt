package com.example.todolistapp.repositories

import com.example.todolistapp.models.*
import com.example.todolistapp.services.ItemAPIService
import retrofit2.Call

interface ItemRepositoryInterface {
    fun getAllItems(): Call<GetAllItemsResponse>
    fun getItemById(itemId: Int): Call<GetItemResponse>
    fun getItemsByBook(bookId: Int?, date: String?): Call<GetAllItemsResponse>
    fun createItem(request: ItemCreateRequest): Call<PostItemResponse>
    fun updateItem(itemId: Int, request: ItemCreateRequest): Call<PostItemResponse>
    fun deleteItem(itemId: Int): Call<DeleteItemResponse>
}

class ItemRepository(
    private val itemAPIService: ItemAPIService
) : ItemRepositoryInterface {

    override fun getAllItems(): Call<GetAllItemsResponse> =
        itemAPIService.getAllItems()

    override fun getItemById(itemId: Int): Call<GetItemResponse> =
        itemAPIService.getItemById(itemId)

    override fun getItemsByBook(bookId: Int?, date: String?): Call<GetAllItemsResponse> =
        itemAPIService.getItemsByBook(bookId, date)

    override fun createItem(request: ItemCreateRequest): Call<PostItemResponse> =
        itemAPIService.createItem(request)

    override fun updateItem(itemId: Int, request: ItemCreateRequest): Call<PostItemResponse> =
        itemAPIService.updateItem(itemId, request)

    override fun deleteItem(itemId: Int): Call<DeleteItemResponse> =
        itemAPIService.deleteItem(itemId)
}