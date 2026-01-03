package com.example.todolistapp.uiStates

import com.example.todolistapp.models.ItemModel

sealed interface ItemDetailStatusUIState {
    object Start : ItemDetailStatusUIState
    object Loading : ItemDetailStatusUIState
    data class Success(val data: ItemModel) : ItemDetailStatusUIState
    data class Failed(val error: String?) : ItemDetailStatusUIState
}