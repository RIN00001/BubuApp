package com.example.todolistapp.models

import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("type")
    val type: String = "EXPENSE",

    @SerializedName("icon")
    val iconKey: String? = null
)

data class GetAllCategoriesResponse(
    @SerializedName("data")
    val data: List<CategoryModel>,

    @SerializedName("message")
    val message: String? = null
)