package com.example.todolistapp.utils

import com.example.todolistapp.repositories.UserRepositoryInterface

object GlobalUtil {
    suspend fun resetUsernameToken(userRepository: UserRepositoryInterface) {
        userRepository.saveUserToken("Unknown")
        userRepository.saveUsername("Unknown")
    }
}