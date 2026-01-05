package com.example.todolistapp.uiStates

import com.example.todolistapp.models.SavingModel

sealed class SavingListUIState {
    object Loading : SavingListUIState()
    data class Success(val savings: List<SavingModel>) : SavingListUIState()
    object Error : SavingListUIState()
}

