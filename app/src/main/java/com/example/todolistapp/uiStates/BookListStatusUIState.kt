package com.example.todolistapp.uiStates

import com.example.todolistapp.models.BookModel

sealed class BookListStatusUIState {
    object Start : BookListStatusUIState()
    object Loading : BookListStatusUIState()
    data class Success(val data: List<BookModel>) : BookListStatusUIState()
    data class Failed(val error: String?) : BookListStatusUIState()
}