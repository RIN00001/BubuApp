package com.example.todolistapp.repositories

import com.example.todolistapp.services.CategoryAPIService
import com.example.todolistapp.services.CategoryActionResponse
import com.example.todolistapp.services.CategoryRequest
import com.example.todolistapp.models.GetAllCategoriesResponse
import retrofit2.Response

interface CategoryRepositoryInterface {
    suspend fun getAllCategories(type: String? = null): Response<GetAllCategoriesResponse>
    suspend fun createCategory(name: String, type: String, icon: String?): Response<CategoryActionResponse>
    suspend fun updateCategory(id: Int, name: String, type: String, icon: String?): Response<CategoryActionResponse>
    suspend fun deleteCategory(categoryId: Int): Response<CategoryActionResponse>
}

class CategoryRepository(
    private val categoryAPIService: CategoryAPIService
) : CategoryRepositoryInterface {

    override suspend fun getAllCategories(type: String?): Response<GetAllCategoriesResponse> {
        return categoryAPIService.getAllCategories(type)
    }

    override suspend fun createCategory(name: String, type: String, icon: String?): Response<CategoryActionResponse> {
        val request = CategoryRequest(name, type, icon)
        return categoryAPIService.createCategory(request)
    }

    override suspend fun updateCategory(id: Int, name: String, type: String, icon: String?): Response<CategoryActionResponse> {
        val request = CategoryRequest(name, type, icon)
        return categoryAPIService.updateCategory(id, request)
    }

    override suspend fun deleteCategory(categoryId: Int): Response<CategoryActionResponse> {
        return categoryAPIService.deleteCategory(categoryId)
    }
}