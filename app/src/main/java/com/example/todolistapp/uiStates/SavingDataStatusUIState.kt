package com.example.todolistapp.uiStates

import com.example.todolistapp.models.SavingModel

sealed interface SavingDataStatusUIState {
    data class Success(val data: List<SavingModel>): SavingDataStatusUIState
    object Start: SavingDataStatusUIState
    object Loading: SavingDataStatusUIState
    data class Failed(val errorMessage: String): SavingDataStatusUIState
}