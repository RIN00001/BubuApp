package com.example.todolistapp.uiStates

import com.example.todolistapp.models.TodoModel

sealed interface TodoDetailDataStatusUIState {
    data class Success(val data: TodoModel): TodoDetailDataStatusUIState
    object Loading: TodoDetailDataStatusUIState
    object Start: TodoDetailDataStatusUIState
    data class Failed(val errorMessage: String): TodoDetailDataStatusUIState
}