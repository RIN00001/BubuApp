package com.example.todolistapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.todolistapp.interceptor.TokenInterceptor
import com.example.todolistapp.repositories.AuthenticationRepository
import com.example.todolistapp.repositories.AuthenticationRepositoryInterface
import com.example.todolistapp.repositories.UserRepository
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.repositories.BookRepository
import com.example.todolistapp.repositories.BookRepositoryInterface
import com.example.todolistapp.repositories.WalletRepository
import com.example.todolistapp.repositories.WalletRepositoryInterface
import com.example.todolistapp.services.AuthenticationAPIService
import com.example.todolistapp.services.BookAPIService
import com.example.todolistapp.services.WalletAPIService

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainerInterface {
    val authenticationRepository: AuthenticationRepositoryInterface
    val userRepository: UserRepositoryInterface

    // NEW MODULES
    val bookRepository: BookRepositoryInterface
    val walletRepository: WalletRepositoryInterface
}

class AppContainer (
    private val dataStore: DataStore<Preferences>
) : AppContainerInterface {

    private val backendURL = "http://192.168.0.30:3000/"

    // FIRST: USER REPO (required for TokenInterceptor)
    private val _userRepository: UserRepositoryInterface by lazy {
        UserRepository(dataStore)
    }

    // RETROFIT SERVICES -----------------------------------------------------

    private val authenticationService: AuthenticationAPIService by lazy {
        val retrofit = initRetrofit(_userRepository)
        retrofit.create(AuthenticationAPIService::class.java)
    }

    private val bookService: BookAPIService by lazy {
        val retrofit = initRetrofit(_userRepository)
        retrofit.create(BookAPIService::class.java)
    }

    private val walletService: WalletAPIService by lazy {
        val retrofit = initRetrofit(_userRepository)
        retrofit.create(WalletAPIService::class.java)
    }

    // REPOSITORIES ----------------------------------------------------------

    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authenticationService)
    }

    override val userRepository: UserRepositoryInterface
        get() = _userRepository

    override val bookRepository: BookRepositoryInterface by lazy {
        BookRepository(bookService)
    }

    override val walletRepository: WalletRepositoryInterface by lazy {
        WalletRepository(walletService)
    }

    // RETROFIT INITIALIZATION ----------------------------------------------

    private fun initRetrofit(userRepo: UserRepositoryInterface): Retrofit {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(TokenInterceptor(userRepo)) // Injects "Bearer token"
            .build()

        return Retrofit.Builder()
            .baseUrl(backendURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}
