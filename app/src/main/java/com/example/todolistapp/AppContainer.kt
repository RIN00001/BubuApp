package com.example.todolistapp

import android.content.Context
import com.example.todolistapp.interceptor.TokenInterceptor
import com.example.todolistapp.repositories.*
import com.example.todolistapp.services.*
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
    val authenticationRepository: AuthenticationRepository
    val userRepository: UserRepository
    val itemRepository: ItemRepository
    val categoryRepository: CategoryRepository

    // Tambahan Wajib (agar tidak error di ViewModel)
    val bookRepository: BookRepository
    val walletRepository: WalletRepository
}

class AppContainer(private val context: Context) : AppContainerInterface {

    // 1. Base URL (Pastikan IP benar, 10.0.2.2 untuk Emulator Android Studio)
    private val backendURL = "http://10.0.2.2:3000/"

    // 2. User Repository
    // context.dataStore otomatis mengambil dari definisi di BubuApplication.kt
    override val userRepository: UserRepository by lazy {
        UserRepository(context.dataStore)
    }

    // 3. Interceptor
    private val tokenInterceptor = TokenInterceptor(userRepository)

    // 4. Client & Retrofit
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(tokenInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(backendURL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    // 5. Services (API Service)
    private val authenticationService: AuthenticationAPIService by lazy {
        retrofit.create(AuthenticationAPIService::class.java)
    }
    private val itemAPIService: ItemAPIService by lazy {
        retrofit.create(ItemAPIService::class.java)
    }
    private val categoryAPIService: CategoryAPIService by lazy {
        retrofit.create(CategoryAPIService::class.java)
    }
    // Tambahkan Service Book & Wallet
    private val bookAPIService: BookAPIService by lazy {
        retrofit.create(BookAPIService::class.java)
    }
    private val walletAPIService: WalletAPIService by lazy {
        retrofit.create(WalletAPIService::class.java)
    }

    // 6. Repositories (Penghubung Data ke ViewModel)
    override val authenticationRepository: AuthenticationRepository by lazy {
        AuthenticationRepository(authenticationService)
    }

    override val itemRepository: ItemRepository by lazy {
        ItemRepository(itemAPIService)
    }

    override val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(categoryAPIService)
    }

    // Tambahkan Repository Book & Wallet
    override val bookRepository: BookRepository by lazy {
        BookRepository(bookAPIService)
    }

    override val walletRepository: WalletRepository by lazy {
        WalletRepository(walletAPIService)
    }
}