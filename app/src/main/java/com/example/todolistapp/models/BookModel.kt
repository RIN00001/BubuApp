package com.example.todolistapp.models

data class BookModel(
    val id: Int = 0,
    val userId: Int? = null,
    val name: String = "",
    val program: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val wallets: List<WalletModel>? = null,
    val items: List<ItemModel>? = null
)

data class BookCreateRequest(
    val name: String,
    val program: String? = null,
    val walletIds: List<Int>? = null
)

data class GetAllBooksResponse(
    val data: List<BookModel>
)

data class GetBookResponse(
    val data: BookModel
)

data class PostBookResponse(
    val data: BookModel
)

data class DeleteBookResponse(
    val data: Any?
)