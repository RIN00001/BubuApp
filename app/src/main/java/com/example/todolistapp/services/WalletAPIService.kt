package com.example.todolistapp.services

import com.example.todolistapp.models.*
import retrofit2.Call
import retrofit2.http.*

interface WalletAPIService {
    @GET("api/wallets")
    fun getAllWallets(): Call<GetAllWalletsResponse>

    @GET("api/wallets/{id}")
    fun getWalletById(@Path("id") walletId: Int): Call<GetWalletResponse>

    @POST("api/wallets")
    fun createWallet(@Body request: WalletRequest): Call<PostWalletResponse>

    @PUT("api/wallets/{id}")
    fun updateWallet(@Path("id") walletId: Int, @Body request: WalletRequest): Call<PostWalletResponse>

    @DELETE("api/wallets/{id}")
    fun deleteWallet(@Path("id") walletId: Int): Call<DeleteWalletResponse>

    @PATCH("api/wallets/{id}/default")
    fun setDefaultWallet(@Path("id") walletId: Int): Call<SetDefaultWalletResponse>

    @GET("api/wallets/{id}/summary")
    fun getWalletSummary(
        @Path("id") walletId: Int,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Call<GetWalletSummaryResponse>
}