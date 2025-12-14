package com.example.todolistapp.uiStates

sealed class WalletMutationStatusUIState {
    object Start : WalletMutationStatusUIState()
    object Loading : WalletMutationStatusUIState()
    data class Success(val message: String?) : WalletMutationStatusUIState()
    data class Failed(val error: String?) : WalletMutationStatusUIState()
}