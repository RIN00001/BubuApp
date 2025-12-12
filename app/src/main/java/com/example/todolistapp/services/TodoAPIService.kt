package com.example.todolistapp.services

import com.example.todolistapp.models.GeneralResponseModel
import com.example.todolistapp.models.GetAllTodoResponse
import com.example.todolistapp.models.GetTodoResponse
import com.example.todolistapp.models.TodoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoAPIService {
    @GET("api/todo-list")
    fun getAllTodos(@Header("Authorization") token: String): Call<GetAllTodoResponse>

    @GET("api/todo-list/{id}")
    fun getTodo(@Header("Authorization") token: String, @Path("id") todoId: Int): Call<GetTodoResponse>

    @POST("api/todo-list")
    fun createTodo(@Header("Authorization") token: String, @Body todoModel: TodoRequest): Call<GeneralResponseModel>

    @PUT("api/todo-list/{id}")
    fun updateTodo(@Header("Authorization") token: String, @Path("id") todoId: Int, @Body todoModel: TodoRequest): Call<GeneralResponseModel>

    @DELETE("api/todo-list/{id}")
    fun deleteTodo(@Header("Authorization") token: String, @Path("id") todoId: Int): Call<GeneralResponseModel>
}