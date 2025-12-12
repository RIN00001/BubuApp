package com.example.todolistapp.uiStates

import com.example.todolistapp.models.UserModel

sealed interface AuthenticatonStatusUIState {
    data class Success(val userModelData: UserModel): AuthenticatonStatusUIState
    object Loading: AuthenticatonStatusUIState
    object Start: AuthenticatonStatusUIState
    data class Failed(val errorMessage: String): AuthenticatonStatusUIState
}