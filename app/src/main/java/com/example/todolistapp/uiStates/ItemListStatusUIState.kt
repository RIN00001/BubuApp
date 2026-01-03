package com.example.todolistapp.uiStates

import com.example.todolistapp.models.ItemModel

sealed interface ItemListStatusUIState {
    object Start : ItemListStatusUIState
    object Loading : ItemListStatusUIState
    data class Success(val data: List<ItemModel>) : ItemListStatusUIState
    data class Failed(val error: String?) : ItemListStatusUIState
}