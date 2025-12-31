package com.example.todolistapp.models

import com.google.gson.annotations.SerializedName



data class ItemCreateRequest(
    @SerializedName("bookId")
    val bookId: Int? = null,

    @SerializedName("walletId")
    val walletId: Int? = null,

    @SerializedName("categoryId")
    val categoryId: Int? = null,

    @SerializedName("type")
    val type: String,

    @SerializedName("amount")
    val amount: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("date")
    val date: String
)



data class ItemModel(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("amount")
    val amount: Long = 0,

    @SerializedName("type")
    val type: String = "EXPENSE",

    @SerializedName("date")
    val date: String = "",

    @SerializedName("category_id")
    val categoryId: Int? = 1,

    @SerializedName("wallet_id")
    val walletId: Int? = 1,

    @SerializedName("book_id")
    val bookId: Int? = 1
)

data class GetAllItemsResponse(
    @SerializedName("data") val data: List<ItemModel>
)

data class PostItemResponse(
    @SerializedName("data") val data: ItemModel
)

data class DeleteItemResponse(
    @SerializedName("data") val data: Any?
)