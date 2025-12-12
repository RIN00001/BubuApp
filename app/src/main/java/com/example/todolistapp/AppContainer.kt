package com.example.todolistapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.todolistapp.repositories.AuthenticationRepository
import com.example.todolistapp.repositories.AuthenticationRepositoryInterface
import com.example.todolistapp.repositories.TodoRepository
import com.example.todolistapp.repositories.TodoRepositoryInterface
import com.example.todolistapp.repositories.UserRepository
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.services.AuthenticationAPIService
import com.example.todolistapp.services.TodoAPIService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainerInterface {
    val authenticationRepository: AuthenticationRepositoryInterface
    val userRepository: UserRepositoryInterface
    val todoRepository: TodoRepositoryInterface
}

class AppContainer (
    private val dataStore: DataStore<Preferences>
): AppContainerInterface {
//    private val backendURL = "http://192.186.12.0:3000/"
    private val backendURL = "http://192.168.18.113:3000/"

    // RETROFIT SERVICE
    private val authenticationRetrofitService: AuthenticationAPIService by lazy {
        val retrofit = initRetrofit()

        retrofit.create(AuthenticationAPIService::class.java)
    }

    private val todoAPIService: TodoAPIService by lazy {
        val retrofit = initRetrofit()

        retrofit.create(TodoAPIService::class.java)
    }

    // REPOSITORY INIT
    override val authenticationRepository: AuthenticationRepositoryInterface by lazy {
        AuthenticationRepository(authenticationRetrofitService)
    }

    override val userRepository: UserRepositoryInterface by lazy {
        UserRepository(dataStore)
    }

    override val todoRepository: TodoRepositoryInterface by lazy {
        TodoRepository(todoAPIService)
    }

    private fun initRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = (HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
        client.addInterceptor(logging)

        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).client(client.build()).baseUrl(backendURL).build()
    }
}