package com.example.todolistapp.repositories

import com.example.todolistapp.models.*
import com.example.todolistapp.services.WalletAPIService
import retrofit2.Call

interface WalletRepositoryInterface {
    fun getAllWallets(): Call<GetAllWalletsResponse>
    fun getWalletById(walletId: Int): Call<GetWalletResponse>
    fun createWallet(request: WalletRequest): Call<PostWalletResponse>
    fun updateWallet(walletId: Int, request: WalletRequest): Call<PostWalletResponse>
    fun deleteWallet(walletId: Int): Call<DeleteWalletResponse>
    fun setDefaultWallet(walletId: Int): Call<SetDefaultWalletResponse>
    fun getWalletSummary(walletId: Int, startDate: String?, endDate: String?): Call<GetWalletSummaryResponse>
}

class WalletRepository(
    private val api: WalletAPIService
) : WalletRepositoryInterface {

    override fun getAllWallets(): Call<GetAllWalletsResponse> = api.getAllWallets()
    override fun getWalletById(walletId: Int): Call<GetWalletResponse> = api.getWalletById(walletId)
    override fun createWallet(request: WalletRequest): Call<PostWalletResponse> = api.createWallet(request)
    override fun updateWallet(walletId: Int, request: WalletRequest): Call<PostWalletResponse> = api.updateWallet(walletId, request)
    override fun deleteWallet(walletId: Int): Call<DeleteWalletResponse> = api.deleteWallet(walletId)
    override fun setDefaultWallet(walletId: Int): Call<SetDefaultWalletResponse> = api.setDefaultWallet(walletId)
    override fun getWalletSummary(walletId: Int, startDate: String?, endDate: String?): Call<GetWalletSummaryResponse> =
        api.getWalletSummary(walletId, startDate, endDate)
}
