package com.example.todolistapp.uiStates

sealed class BookMutationStatusUIState {
    object Start : BookMutationStatusUIState()
    object Loading : BookMutationStatusUIState()
    data class Success(val message: String? = null) : BookMutationStatusUIState()
    data class Failed(val error: String?) : BookMutationStatusUIState()
}