package com.example.todolistapp.models

data class UserResponse (
    val data: UserModel
)


data class UserModel (
    val token: String?
)