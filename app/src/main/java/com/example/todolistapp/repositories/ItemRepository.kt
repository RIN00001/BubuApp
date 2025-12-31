package com.example.todolistapp.repositories

import com.example.todolistapp.models.DeleteItemResponse
import com.example.todolistapp.models.GetAllItemsResponse
import com.example.todolistapp.models.ItemCreateRequest
import com.example.todolistapp.models.PostItemResponse
import com.example.todolistapp.services.ItemAPIService
import retrofit2.Call

interface ItemRepositoryInterface {
    fun getAllItems(): Call<GetAllItemsResponse>
    fun getItemsByID(itemId: Int): Call<PostItemResponse>
    fun createItem(request: ItemCreateRequest): Call<PostItemResponse>
    fun updateItem(itemId: Int, request: ItemCreateRequest): Call<PostItemResponse>
    fun deleteItem(itemId: Int): Call<DeleteItemResponse>
}



class ItemRepository(
    private val itemAPIService: ItemAPIService
): ItemRepositoryInterface {
    override fun getAllItems(): Call<GetAllItemsResponse> {
        return itemAPIService.getAllItems()
    }
    override fun getItemsByID(itemId: Int): Call<PostItemResponse> {
        return itemAPIService.getItemById(itemId)
    }
    override fun createItem(request: ItemCreateRequest): Call<PostItemResponse> {
        return itemAPIService.createItem(request)
    }
    override fun updateItem(itemId: Int, request: ItemCreateRequest): Call<PostItemResponse> {
        return itemAPIService.updateItem(itemId, request)
    }
    override fun deleteItem(itemId: Int): Call<DeleteItemResponse> {
        return itemAPIService.deleteItem(itemId)
    }
}