package com.example.todolistapp.models

data class ItemModel(
    val id: Int = 0,
    val name: String = "",
    val amount: Double = 0.0,
    val type: String = "", // Either EXPENSE or INCOME
    val date: String = ""
)
