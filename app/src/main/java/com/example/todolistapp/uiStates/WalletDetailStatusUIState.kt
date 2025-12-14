package com.example.todolistapp.uiStates

import com.example.todolistapp.models.WalletModel

sealed class WalletDetailStatusUIState {
    object Start : WalletDetailStatusUIState()
    object Loading : WalletDetailStatusUIState()
    data class Success(val data: WalletModel) : WalletDetailStatusUIState()
    data class Failed(val error: String?) : WalletDetailStatusUIState()
}