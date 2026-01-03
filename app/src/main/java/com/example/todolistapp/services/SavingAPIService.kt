package com.example.todolistapp.services

import com.example.todolistapp.models.GeneralResponseModel
import com.example.todolistapp.models.GetSavingResponse
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.models.SavingRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SavingAPIService {
    @GET("api/savings")
    fun getAllSavings(@Header("Authorization") token: String): Call<List<SavingModel>>

    @GET("api/saving/{id}")
    fun getSaving(@Header("Authorization") token: String, @Path("id") id: Int): Call<GetSavingResponse>

    @POST("api/saving")
    fun createSaving(@Header("Authorization") token: String, @Body savingModel: SavingRequest): Call<GeneralResponseModel>

    @PUT("api/saving/{id}")
    fun updateSaving(@Header("Authorization") token: String, @Path("id") id: Int, @Body todoModel:SavingRequest): Call<GeneralResponseModel>

    @DELETE("api/saving/{id}")
    fun deleteSaving(@Header("Authorization") token: String, @Path("id") id: Int): Call<GeneralResponseModel>
}