package com.example.todolistapp.models

data class WalletModel(
    val id: Int = 0,
    val userId: Int? = null,
    val name: String = "",
    var balance: Double = 0.0,
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

// Wallet Summary Models
data class UsedInBook(
    val id: Int,
    val name: String,
    val program: String
)

data class WalletSummary(
    val id: Int,
    val name: String,
    val balance: Double,
    val isDefault: Boolean,
    val totalIncome: Double,
    val totalExpense: Double,
    val netFlow: Double,
    val usedInBooks: List<UsedInBook>
)

data class GetWalletSummaryResponse(
    val data: WalletSummary
)

// Book Wallet Models
data class BookWalletItem(
    val id: Int,
    val name: String,
    val balance: Double,
    val isDefault: Boolean
)

data class BookWalletsData(
    val bookId: Int,
    val bookName: String,
    val wallets: List<BookWalletItem>
)

data class GetBookWalletsResponse(
    val data: BookWalletsData
)

data class AttachWalletResponse(
    val data: Any?
)

data class DetachWalletResponse(
    val data: Any?
)
