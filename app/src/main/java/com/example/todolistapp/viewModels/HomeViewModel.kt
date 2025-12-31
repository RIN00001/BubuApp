package com.example.todolistapp.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.TodoListApplication
import com.example.todolistapp.models.DeleteItemResponse
import com.example.todolistapp.models.GetAllItemsResponse
import com.example.todolistapp.models.ItemModel
import com.example.todolistapp.repositories.ItemRepositoryInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Status UI
sealed interface HomeUIState {
    data class Success(val items: List<ItemModel>) : HomeUIState
    object Error : HomeUIState
    object Loading : HomeUIState
}

class HomeViewModel(
    private val itemRepository: ItemRepositoryInterface
) : ViewModel() {

    var homeUIState: HomeUIState by mutableStateOf(HomeUIState.Loading)
        private set

    init {
        getAllItems()
    }


    fun getAllItems() {
        homeUIState = HomeUIState.Loading


        val call = itemRepository.getAllItems()


        call.enqueue(object : Callback<GetAllItemsResponse> {
            override fun onResponse(
                call: Call<GetAllItemsResponse>,
                response: Response<GetAllItemsResponse>
            ) {
                if (response.isSuccessful) {
                    val items = response.body()?.data ?: emptyList()
                    homeUIState = HomeUIState.Success(items)
                } else {
                    homeUIState = HomeUIState.Error
                }
            }

            override fun onFailure(call: Call<GetAllItemsResponse>, t: Throwable) {
                t.printStackTrace()
                homeUIState = HomeUIState.Error
            }
        })
    }


    fun deleteItem(itemId: Int) {
        val call = itemRepository.deleteItem(itemId)

        call.enqueue(object : Callback<DeleteItemResponse> {
            override fun onResponse(
                call: Call<DeleteItemResponse>,
                response: Response<DeleteItemResponse>
            ) {
                if (response.isSuccessful) {

                    getAllItems()
                }
            }

            override fun onFailure(call: Call<DeleteItemResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as TodoListApplication)
                HomeViewModel(itemRepository = app.container.itemRepository)
            }
        }
    }
}