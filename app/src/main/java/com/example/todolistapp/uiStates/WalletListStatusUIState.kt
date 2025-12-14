package com.example.todolistapp.uiStates

import com.example.todolistapp.models.WalletModel

sealed class WalletListStatusUIState {
    object Start : WalletListStatusUIState()
    object Loading : WalletListStatusUIState()
    data class Success(val data: List<WalletModel>) : WalletListStatusUIState()
    data class Failed(val error: String?) : WalletListStatusUIState()
}