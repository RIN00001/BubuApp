package com.example.todolistapp.Interceptor

import com.example.todolistapp.repositories.UserRepositoryInterface
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val userRepository: UserRepositoryInterface
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = try {
            runBlocking {
                userRepository.currentUserToken.first()
            }
        } catch (ex: Exception) {
            null
        }

        val request = if (!token.isNullOrBlank() && token != "Unknown") {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}