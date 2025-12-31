package com.example.todolistapp.uiStates

import com.example.todolistapp.models.BookModel

sealed class BookDetailStatusUIState {
    object Start : BookDetailStatusUIState()
    object Loading : BookDetailStatusUIState()
    data class Success(val data: BookModel) : BookDetailStatusUIState()
    data class Failed(val error: String?) : BookDetailStatusUIState()

}