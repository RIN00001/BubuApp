package com.example.todolistapp.uiStates

sealed interface SavingDetailDataStatusUIState {
    data class Success(val data: String): SavingDetailDataStatusUIState
    object Loading: SavingDetailDataStatusUIState
    object Start: SavingDetailDataStatusUIState
    data class Failed(val errorMessage: String): SavingDetailDataStatusUIState
}