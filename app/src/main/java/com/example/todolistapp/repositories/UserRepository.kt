package com.example.todolistapp.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface UserRepositoryInterface {
    val currentUserToken: Flow<String>
    val currentUsername: Flow<String>

    suspend fun saveUserToken(token: String)
    suspend fun saveUsername(username: String)

    suspend fun logout()
}

class UserRepository (
    private val userDataStore: DataStore<Preferences>
): UserRepositoryInterface {
    override val currentUserToken: Flow<String> = userDataStore.data.map { preferences -> preferences[USER_TOKEN] ?: "Unknown" }
    override val currentUsername: Flow<String> = userDataStore.data.map { preferences -> preferences[USERNAME] ?: "Unknown" }

    override suspend fun saveUserToken(token: String) {
        userDataStore.edit { preferences -> preferences[USER_TOKEN] = token }
    }

    override suspend fun saveUsername(username: String) {
        userDataStore.edit { preferences -> preferences[USERNAME] = username }
    }

    override suspend fun logout() {
        userDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private companion object {
        val USER_TOKEN = stringPreferencesKey("token")
        val USERNAME = stringPreferencesKey("username")
    }
}