package com.example.todolistapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.todolistapp.utils.TokenManager

// INI DEFINISI UTAMA DATASTORE (Jangan pakai private)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

class BubuApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        container = AppContainer(context = this)
    }
}