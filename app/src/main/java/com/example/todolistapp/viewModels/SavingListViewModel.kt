package com.example.todolistapp.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.repositories.SavingRepositoryInterface
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.uiStates.SavingListUIState
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavingListViewModel(
    private val savingRepository: SavingRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    var savingListUIState: SavingListUIState by mutableStateOf(SavingListUIState.Loading)
        private set

    init {
        getAllSavings("")
    }

    fun getAllSavings(token: String) {
        if (token.isEmpty()) return

        savingListUIState = SavingListUIState.Loading

        val call = savingRepository.getAllSavings(token)

        call.enqueue(object : Callback<List<SavingModel>> {
            override fun onResponse(
                call: Call<List<SavingModel>>,
                response: Response<List<SavingModel>>
            ) {
                if (response.isSuccessful) {
                    val listData = response.body()

                    if (listData != null) {
                        savingListUIState = SavingListUIState.Success(listData)
                        Log.d("SavingListViewModel", "Loaded ${listData.size} savings")
                    } else {
                        savingListUIState = SavingListUIState.Success(emptyList())
                    }
                } else {
                    Log.e("SavingListViewModel", "Error Code: ${response.code()}")
                    savingListUIState = SavingListUIState.Error
                }
            }

            override fun onFailure(call: Call<List<SavingModel>>, t: Throwable) {
                Log.e("SavingListViewModel", "Network Error: ${t.message}")
                savingListUIState = SavingListUIState.Error
            }
        })
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BubuApplication)
                val savingRepository = application.container.savingRepository
                val userRepository = application.container.userRepository
                SavingListViewModel(
                    savingRepository = savingRepository,
                    userRepository = userRepository
                )
            }
        }
    }
}

