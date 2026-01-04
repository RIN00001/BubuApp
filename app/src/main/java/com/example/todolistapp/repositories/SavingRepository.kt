package com.example.todolistapp.repositories

import com.example.todolistapp.models.GeneralResponseModel
import com.example.todolistapp.models.GetSavingResponse
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.models.SavingRequest
import com.example.todolistapp.services.SavingAPIService
import retrofit2.Call

interface SavingRepositoryInterface {
    fun getAllSavings(token: String): Call<List<SavingModel>>

    fun createSaving(token: String, userId: Int, id: Int, amount: Double, targetamount: Double, name: String, goalDate: String, walletIds: List<Int>): Call<GeneralResponseModel>

    fun getSaving(token: String, userId: Int, id: Int): Call<GetSavingResponse>

    fun updateSaving(token: String, userId: Int, id: Int, amount: Double, targetamount: Double, name: String, goalDate: String, walletIds: List<Int>): Call<GeneralResponseModel>

    fun deleteSaving(token: String, userId: Int, id: Int): Call<GeneralResponseModel>
}

class SavingRepository(
    private val SavingAPIService: SavingAPIService
): SavingRepositoryInterface {
    override fun getAllSavings(token: String): Call<List<SavingModel>> {
        return SavingAPIService.getAllSavings("Bearer ${token}")
    }

    override fun createSaving(
        token: String,
        userId: Int,
        id: Int,
        amount: Double,
        targetamount: Double,
        name: String,
        goalDate: String,
        walletIds: List<Int>
    ): Call<GeneralResponseModel> {
        return SavingAPIService.createSaving(
            "Bearer ${token}",
            SavingRequest(
                userId = userId,
                amount = amount,
                targetamount = targetamount,
                name = name,
                goalDate = goalDate,
                walletIds = walletIds
            )
        )
    }

    override fun getSaving(
        token: String,
        userId: Int,
        id: Int
    ): Call<GetSavingResponse> {
        return SavingAPIService.getSaving("Bearer ${token}", id)
    }

    override fun updateSaving(
        token: String,
        userId: Int,
        id: Int,
        amount: Double,
        targetamount: Double,
        name: String,
        goalDate: String,
        walletIds: List<Int>
    ): Call<GeneralResponseModel> {
        return SavingAPIService.updateSaving(
            "Bearer ${token}",
            id,
            SavingRequest(
                userId = userId,
                amount = amount,
                targetamount = targetamount,
                name = name,
                goalDate = goalDate,
                walletIds = walletIds
            )
        )
    }

    override fun deleteSaving(
        token: String,
        userId: Int,
        id: Int
    ): Call<GeneralResponseModel> {
        return SavingAPIService.deleteSaving("Bearer ${token}", id)
    }
}
