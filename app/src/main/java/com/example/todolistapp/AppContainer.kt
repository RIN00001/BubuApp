package com.example.todolistapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.auth0.android.jwt.BuildConfig
import com.example.todolistapp.repositories.*
import com.example.todolistapp.services.*
import com.example.todolistapp.utils.TokenManager
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
}

class AppContainer(
    private val dataStore: DataStore<Preferences>
) : AppContainerInterface {

    private val backendURL = "http://10.0.2.2:3000/"

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

    override val itemRepository: ItemRepositoryInterface by lazy {
        ItemRepository(itemAPIService)
    }

    // TAMBAHAN: Repo Category
    override val categoryRepository: CategoryRepositoryInterface by lazy {
        CategoryRepository(categoryAPIService)
    }
}