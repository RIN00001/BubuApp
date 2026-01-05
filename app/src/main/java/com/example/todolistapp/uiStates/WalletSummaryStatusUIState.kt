package com.example.todolistapp.uiStates

import com.example.todolistapp.models.WalletSummary

sealed class WalletSummaryStatusUIState {
    data object Start : WalletSummaryStatusUIState()
    data object Loading : WalletSummaryStatusUIState()
    data class Success(val data: WalletSummary) : WalletSummaryStatusUIState()
    data class Failed(val error: String?) : WalletSummaryStatusUIState()
}

