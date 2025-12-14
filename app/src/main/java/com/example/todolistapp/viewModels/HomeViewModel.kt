package com.example.todolistapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.repositories.UserRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    val username: StateFlow<String> = userRepository.currentUsername.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    val token: StateFlow<String> = userRepository.currentUserToken.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ""
    )

    fun logout(navController: NavHostController) {
        viewModelScope.launch {
            userRepository.saveUserToken("")
            userRepository.saveUsername("")
        }

        navController.navigate(PagesEnum.Login.name) {
            popUpTo(PagesEnum.Home.name) { inclusive = true }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as BubuApplication)

                HomeViewModel(
                    userRepository = application.container.userRepository
                )
            }
        }
    }
}
