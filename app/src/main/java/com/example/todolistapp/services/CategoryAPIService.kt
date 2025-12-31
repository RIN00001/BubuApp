package com.example.todolistapp.services

import com.example.todolistapp.models.CategoryModel
import com.example.todolistapp.models.GetAllCategoriesResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

// Model Body untuk Create & Update (Isinya sama: Name, Type, Icon)
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

    @GET("api/categories")
    suspend fun getAllCategories(
        @Query("type") type: String? = null
    ): Response<GetAllCategoriesResponse>

    @POST("api/categories")
    suspend fun createCategory(
        @Body request: CategoryRequest
    ): Response<CategoryActionResponse>

    // --- NEW: UPDATE ---
    @PUT("api/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body request: CategoryRequest
    ): Response<CategoryActionResponse>

    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(
        @Path("id") categoryId: Int
    ): Response<CategoryActionResponse>
}