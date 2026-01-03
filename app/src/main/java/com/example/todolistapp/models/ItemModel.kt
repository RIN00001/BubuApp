package com.example.todolistapp.models

data class ItemModel(
    val id: Int = 0,
    val name: String = "",
    val amount: Double = 0.0,
    val type: String = "EXPENSE", // Default pengeluaran
    val bookId: Int,
    val walletId: Int?,
    val categoryId: Int? = null,
    val date: String = ""
)

// Request body saat membuat item baru
data class ItemCreateRequest(
    val name: String,
    val amount: Double,
    val type: String,
    val bookId: Int,
    val walletId: Int?,
    val categoryId: Int? = null
)

// Response Wrappers (Gaya temanmu)
data class GetAllItemsResponse(val data: List<ItemModel>)
data class GetItemResponse(val data: ItemModel)
data class PostItemResponse(val data: ItemModel)
data class DeleteItemResponse(val data: Any?)