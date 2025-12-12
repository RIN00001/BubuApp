package com.example.todolistapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.todolistapp.repositories.AuthenticationRepository
import com.example.todolistapp.repositories.AuthenticationRepositoryInterface
import com.example.todolistapp.repositories.UserRepository
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.services.AuthenticationAPIService
import com.example.todolistapp.Interceptor.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



interface AppContainerInterface {
    val authenticationRepository: AuthenticationRepositoryInterface
    val userRepository: UserRepositoryInterface
}

class AppContainer(
    private val dataStore: DataStore<Preferences>
) : AppContainerInterface {

    // Emulator-friendly localhost
    private val backendURL = "http://10.0.2.2:3000/"

    // User Repository MUST be initialized first
    private val _userRepository: UserRepositoryInterface by lazy {
        UserRepository(dataStore)
    }

    // Retrofit Services ----------------------------------------

    private val authenticationRetrofitService: AuthenticationAPIService by lazy {
        val retrofit = initRetrofit(_userRepository)
        retrofit.create(AuthenticationAPIService::class.java)
    }

    // Repositories ---------------------------------------------

    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authenticationRetrofitService)
    }

    override val userRepository: UserRepositoryInterface
        get() = _userRepository

    // Retrofit Initialization ----------------------------------

    private fun initRetrofit(userRepository: UserRepositoryInterface): Retrofit {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(TokenInterceptor(userRepository)) // ← NEW interceptor
            .build()

        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(backendURL)
            .build()
    }
}
