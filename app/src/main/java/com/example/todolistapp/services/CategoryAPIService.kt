package com.example.todolistapp.services

import com.example.todolistapp.models.GetAllCategoriesResponse
import com.example.todolistapp.models.CategoryModel
import com.google.gson.annotations.SerializedName
import retrofit2.Call // Pastikan import ini ada
import retrofit2.http.*

data class CategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("icon") val icon: String? = null
)

data class CategoryActionResponse(
    @SerializedName("data") val data: CategoryModel?,
    @SerializedName("message") val message: String?
)

interface CategoryAPIService {
    // TIDAK ADA 'suspend'
    // RETURN TYPE ADALAH 'Call<...>'

    @GET("api/categories")
    fun getAllCategories(
        @Query("type") type: String? = null
    ): Call<GetAllCategoriesResponse>

    @POST("api/categories")
    fun createCategory(
        @Body request: CategoryRequest
    ): Call<CategoryActionResponse>

    @PUT("api/categories/{id}")
    fun updateCategory(
        @Path("id") id: Int,
        @Body request: CategoryRequest
    ): Call<CategoryActionResponse>

    @DELETE("api/categories/{id}")
    fun deleteCategory(
        @Path("id") categoryId: Int
    ): Call<CategoryActionResponse>
}