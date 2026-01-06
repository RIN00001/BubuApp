package com.example.todolistapp

import android.content.Context
import com.example.todolistapp.interceptor.TokenInterceptor
import com.example.todolistapp.repositories.*
import com.example.todolistapp.services.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// 1. Definisikan Interface Semua Repo
interface AppContainerInterface {
    val authenticationRepository: AuthenticationRepositoryInterface
    val userRepository: UserRepositoryInterface
    val itemRepository: ItemRepositoryInterface
    val categoryRepository: CategoryRepositoryInterface
    val bookRepository: BookRepositoryInterface
    val walletRepository: WalletRepositoryInterface

    // NEW: Saving
    val savingRepository: SavingRepositoryInterface
}

class AppContainer(private val context: Context) : AppContainerInterface {

    companion object {
        // Ganti IP jika perlu (10.0.2.2 untuk emulator)
        private const val BASE_URL = "http://10.0.2.2:3000/"
        private const val TIMEOUT_DURATION = 30L // Detik
    }

    // =========================================================================
    // CORE (Network & User)
    // =========================================================================

    override val userRepository: UserRepositoryInterface by lazy {
        UserRepository(context.dataStore)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val tokenInterceptor = TokenInterceptor(userRepository)

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(tokenInterceptor)
            .connectTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_DURATION, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    // =========================================================================
    // SERVICES
    // =========================================================================

    private val authService: AuthenticationAPIService by lazy {
        retrofit.create(
            AuthenticationAPIService::class.java
        )
    }
    private val itemService: ItemAPIService by lazy { retrofit.create(ItemAPIService::class.java) }
    private val categoryService: CategoryAPIService by lazy { retrofit.create(CategoryAPIService::class.java) }
    private val bookService: BookAPIService by lazy { retrofit.create(BookAPIService::class.java) }
    private val walletService: WalletAPIService by lazy { retrofit.create(WalletAPIService::class.java) }
    private val savingService: SavingAPIService by lazy { retrofit.create(SavingAPIService::class.java) }

    // =========================================================================
    // REPOSITORIES
    // =========================================================================

    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authService)
    }

    override val itemRepository: ItemRepositoryInterface by lazy {
        ItemRepository(itemService)
    }

    override val categoryRepository: CategoryRepositoryInterface by lazy {
        CategoryRepository(categoryService)
    }

    override val bookRepository: BookRepositoryInterface by lazy {
        BookRepository(bookService)
    }

    override val walletRepository: WalletRepositoryInterface by lazy {
        WalletRepository(walletService)
    }

    // NEW: Saving Repository
    override val savingRepository: SavingRepositoryInterface by lazy {
        SavingRepository(savingService)
    }
}