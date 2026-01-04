package com.example.todolistapp.models

import com.google.gson.annotations.SerializedName

data class SavingModel(
    val userId: Int,
    val id: Int,
    @SerializedName("currentAmount")
    val amount: Double,
    @SerializedName("targetAmount")
    val targetamount: Double,
    val name : String,

    @SerializedName("goalDate")
    val goalDate: String = ""
)

data class GetAllSavingResponse (
    val data: List<SavingModel>
)

data class GetSavingResponse (
    val data: SavingModel
)

data class SavingRequest (
    val userId: Int,
    @SerializedName("currentAmount")
    val amount: Double,
    @SerializedName("targetAmount")
    val targetamount: Double,
    val name : String,

    val goalDate: String = "",

    val walletIds: List<Int>
)
