package com.example.todolistapp

import android.content.Context
import com.example.todolistapp.interceptor.TokenInterceptor
import com.example.todolistapp.repositories.*
import com.example.todolistapp.services.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 1. Definisikan Interface dengan semua Repository yang dibutuhkan
interface AppContainerInterface {
    val authenticationRepository: AuthenticationRepositoryInterface
    val userRepository: UserRepositoryInterface
    val itemRepository: ItemRepositoryInterface
    val categoryRepository: CategoryRepositoryInterface
    val bookRepository: BookRepositoryInterface
    val walletRepository: WalletRepositoryInterface
}

class AppContainer(private val context: Context) : AppContainerInterface {

    // 2. Base URL (Gunakan 10.0.2.2 untuk Emulator Android Studio)
    private val backendURL = "http://10.0.2.2:3000/"

    // 3. User Repository (Harus pertama karena dibutuhkan oleh Interceptor)
    override val userRepository: UserRepositoryInterface by lazy {
        UserRepository(context.dataStore)
    }

    // 4. Setup Network (Client & Retrofit)
    private val tokenInterceptor by lazy {
        TokenInterceptor(userRepository)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(tokenInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(backendURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    // 5. API Services
    private val authenticationService: AuthenticationAPIService by lazy {
        retrofit.create(AuthenticationAPIService::class.java)
    }
    private val itemAPIService: ItemAPIService by lazy {
        retrofit.create(ItemAPIService::class.java)
    }
    private val categoryAPIService: CategoryAPIService by lazy {
        retrofit.create(CategoryAPIService::class.java)
    }
    private val bookAPIService: BookAPIService by lazy {
        retrofit.create(BookAPIService::class.java)
    }
    private val walletAPIService: WalletAPIService by lazy {
        retrofit.create(WalletAPIService::class.java)
    }

    // 6. Repositories (Implementasi Interface)
    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authenticationService)
    }

    override val itemRepository: ItemRepositoryInterface by lazy {
        ItemRepository(itemAPIService)
    }

    override val categoryRepository: CategoryRepositoryInterface by lazy {
        CategoryRepository(categoryAPIService)
    }

    override val bookRepository: BookRepositoryInterface by lazy {
        BookRepository(bookAPIService)
    }

    override val walletRepository: WalletRepositoryInterface by lazy {
        WalletRepository(walletAPIService)
    }
}