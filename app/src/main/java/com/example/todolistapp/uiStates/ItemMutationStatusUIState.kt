package com.example.todolistapp.uiStates

sealed interface ItemMutationStatusUIState {
    object Start : ItemMutationStatusUIState
    object Loading : ItemMutationStatusUIState
    data class Success(val message: String) : ItemMutationStatusUIState
    data class Failed(val error: String?) : ItemMutationStatusUIState
}