package com.example.todolistapp.uiStates

import com.example.todolistapp.models.ItemModel

sealed interface HomeUIState {
    // State saat data sukses dimuat
    data class Success(val items: List<ItemModel>) : HomeUIState

    // State saat error terjadi
    object Error : HomeUIState

    // State saat loading
    object Loading : HomeUIState
}