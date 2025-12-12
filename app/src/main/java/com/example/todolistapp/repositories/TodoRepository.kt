package com.example.todolistapp.repositories

import com.example.todolistapp.models.GeneralResponseModel
import com.example.todolistapp.models.GetAllTodoResponse
import com.example.todolistapp.models.GetTodoResponse
import com.example.todolistapp.models.TodoRequest
import com.example.todolistapp.services.TodoAPIService
import retrofit2.Call

interface TodoRepositoryInterface {
    fun getAllTodos(token: String): Call<GetAllTodoResponse>

    fun createTodo(token: String, title: String, description: String, dueDate: String, status: String, priority: String): Call<GeneralResponseModel>

    fun getTodo(token: String, todoId: Int): Call<GetTodoResponse>

    fun updateTodo(token: String, todoId: Int, title: String, description: String, dueDate: String, status: String, priority: String): Call<GeneralResponseModel>

    fun deleteTodo(token: String, todoId: Int): Call<GeneralResponseModel>
}

class TodoRepository(
    private val todoAPIService: TodoAPIService
): TodoRepositoryInterface {
    override fun getAllTodos(token: String): Call<GetAllTodoResponse> {
        return todoAPIService.getAllTodos("Bearer ${token}")
    }

    override fun createTodo(
        token: String,
        title: String,
        description: String,
        dueDate: String,
        status: String,
        priority: String
    ): Call<GeneralResponseModel> {
        return todoAPIService.createTodo("Bearer ${token}", TodoRequest(title, status, priority, description, dueDate))
    }

    override fun getTodo(
        token: String,
        todoId: Int
    ): Call<GetTodoResponse> {
        return todoAPIService.getTodo("Bearer ${token}", todoId)
    }

    override fun updateTodo(
        token: String,
        todoId: Int,
        title: String,
        description: String,
        dueDate: String,
        status: String,
        priority: String
    ): Call<GeneralResponseModel> {
        return todoAPIService.updateTodo("Bearer ${token}", todoId, TodoRequest(title, status, priority, description, dueDate))
    }

    override fun deleteTodo(
        token: String,
        todoId: Int
    ): Call<GeneralResponseModel> {
        return todoAPIService.deleteTodo("Bearer ${token}", todoId)
    }
}