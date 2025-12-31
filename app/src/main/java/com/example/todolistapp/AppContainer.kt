package com.example.todolistapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.auth0.android.jwt.BuildConfig
import com.example.todolistapp.repositories.*
import com.example.todolistapp.services.*
import com.example.todolistapp.utils.TokenManager
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
    val itemRepository: ItemRepositoryInterface
    // TAMBAHAN: Interface untuk Category Repo
    val categoryRepository: CategoryRepositoryInterface

    // NEW MODULES
    val bookRepository: BookRepositoryInterface
    val walletRepository: WalletRepositoryInterface
}

class AppContainer (
    private val dataStore: DataStore<Preferences>
) : AppContainerInterface {

    private val backendURL = "http://192.168.1.4:3000/"

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

    private val backendURL = "http://10.0.2.2:3000/"
    // REPOSITORIES ----------------------------------------------------------

    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authenticationService)
    }

    // --- RETROFIT SERVICES ---
    private fun initRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }

        // Interceptor Token (Otomatis ambil dari TokenManager)
        val authInterceptor = okhttp3.Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val token = TokenManager.getToken()
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
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
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(backendURL)
            .build()
    }

    // Lazy instance retrofit (biar gak init berkali-kali)
    private val retrofitClient by lazy { initRetrofit() }

    private val authenticationAPIService: AuthenticationAPIService by lazy {
        retrofitClient.create(AuthenticationAPIService::class.java)
    }

    private val itemAPIService: ItemAPIService by lazy {
        retrofitClient.create(ItemAPIService::class.java)
    }

    // TAMBAHAN: Service Category
    private val categoryAPIService: CategoryAPIService by lazy {
        retrofitClient.create(CategoryAPIService::class.java)
    }

    // --- REPOSITORIES ---
    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authenticationAPIService)
    }

    override val userRepository: UserRepositoryInterface by lazy {
        UserRepository(dataStore)
    }
            .addInterceptor(logging)
            .addInterceptor(TokenInterceptor(userRepo)) // Injects "Bearer token"
            .build()

        return Retrofit.Builder()
            .baseUrl(backendURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    override val itemRepository: ItemRepositoryInterface by lazy {
        ItemRepository(itemAPIService)
    }

    // TAMBAHAN: Repo Category
    override val categoryRepository: CategoryRepositoryInterface by lazy {
        CategoryRepository(categoryAPIService)
    }
}
    }