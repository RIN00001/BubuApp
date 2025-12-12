package com.example.todolistapp.models

data class WalletModel(
    val id: Int = 0,
    val userId: Int? = null,
    val name: String = "",
    val balance: Double = 0.0,
    val isDefault: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class WalletRequest(
    val name: String,
    val balance: Double = 0.0
)

data class GetAllWalletsResponse(
    val data: List<WalletModel>
)

data class GetWalletResponse(
    val data: WalletModel
)

data class PostWalletResponse(
    val data: WalletModel
)

data class DeleteWalletResponse(
    val data: Any?
)

data class SetDefaultWalletResponse(
    val data: WalletModel
)