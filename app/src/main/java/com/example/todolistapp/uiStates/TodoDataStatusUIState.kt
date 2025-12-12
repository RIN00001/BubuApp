package com.example.todolistapp.uiStates

import com.example.todolistapp.models.TodoModel

sealed interface TodoDataStatusUIState {
    data class Success(val data: List<TodoModel>): TodoDataStatusUIState
    object Start: TodoDataStatusUIState
    object Loading: TodoDataStatusUIState
    data class Failed(val errorMessage: String): TodoDataStatusUIState
}