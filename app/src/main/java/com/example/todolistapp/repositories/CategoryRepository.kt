package com.example.todolistapp.repositories

import com.example.todolistapp.services.CategoryAPIService
import com.example.todolistapp.services.CategoryActionResponse
import com.example.todolistapp.services.CategoryRequest
import com.example.todolistapp.models.GetAllCategoriesResponse
import retrofit2.Call // Import Call

interface CategoryRepositoryInterface {
    // HAPUS suspend, GANTI Response dengan Call
    fun getAllCategories(type: String? = null): Call<GetAllCategoriesResponse>
    fun createCategory(name: String, type: String, icon: String?): Call<CategoryActionResponse>
    fun updateCategory(id: Int, name: String, type: String, icon: String?): Call<CategoryActionResponse>
    fun deleteCategory(categoryId: Int): Call<CategoryActionResponse>
}

class CategoryRepository(
    private val categoryAPIService: CategoryAPIService
) : CategoryRepositoryInterface {

    override fun getAllCategories(type: String?): Call<GetAllCategoriesResponse> {
        return categoryAPIService.getAllCategories(type)
    }

    override fun createCategory(name: String, type: String, icon: String?): Call<CategoryActionResponse> {
        val request = CategoryRequest(name, type, icon)
        return categoryAPIService.createCategory(request)
    }

    override fun updateCategory(id: Int, name: String, type: String, icon: String?): Call<CategoryActionResponse> {
        val request = CategoryRequest(name, type, icon)
        return categoryAPIService.updateCategory(id, request)
    }

    override fun deleteCategory(categoryId: Int): Call<CategoryActionResponse> {
        return categoryAPIService.deleteCategory(categoryId)
    }
}