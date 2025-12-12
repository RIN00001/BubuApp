package com.example.todolistapp.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.todolistapp.TodoListApplication
import com.example.todolistapp.enums.PrioritiesEnum
import com.example.todolistapp.models.TodoModel
import com.example.todolistapp.repositories.UserRepository
import com.example.todolistapp.repositories.UserRepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.ErrorModel
import com.example.todolistapp.models.GeneralResponseModel
import com.example.todolistapp.models.GetAllTodoResponse
import com.example.todolistapp.repositories.TodoRepositoryInterface
import com.example.todolistapp.uiStates.StringDataStatusUIState
import com.example.todolistapp.uiStates.TodoDataStatusUIState
import com.example.todolistapp.utils.GlobalUtil
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val userRepository: UserRepositoryInterface,
    private val todoRepository: TodoRepositoryInterface
): ViewModel() {
    private val _todoModel = MutableStateFlow<MutableList<TodoModel>>(mutableListOf())

    val todoModel: StateFlow<List<TodoModel>>
        get() {
            return _todoModel.asStateFlow()
        }

    val username: StateFlow<String> = userRepository.currentUsername.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    val token: StateFlow<String> = userRepository.currentUserToken.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    var getAllTodoStatus: TodoDataStatusUIState by mutableStateOf(TodoDataStatusUIState.Start)
        private set

    fun changePriorityTextBackgroundColor(
        priority: PrioritiesEnum
    ): Color {
        if (priority == PrioritiesEnum.High) {
            return Color.Red
        } else if (priority == PrioritiesEnum.Medium) {
            return Color.Yellow
        }

        return Color.Green
    }

    fun logout(token: String, navController: NavHostController) {
        saveUsernameToken("Unknown", "Unknown")

        navController.navigate(PagesEnum.Login.name) {
            popUpTo(PagesEnum.Home.name) {
                inclusive = true
            }
        }
    }

    fun saveUsernameToken(token: String, username: String) {
        viewModelScope.launch {
            userRepository.saveUserToken(token)
            userRepository.saveUsername(username)
        }
    }

    fun getAllTodos(token: String, navController: NavHostController) {
        viewModelScope.launch {
            getAllTodoStatus = TodoDataStatusUIState.Loading

            try {
                val call = todoRepository.getAllTodos(token = token)

                call.enqueue(object: Callback<GetAllTodoResponse> {
                    override fun onResponse(
                        call: Call<GetAllTodoResponse>,
                        res: Response<GetAllTodoResponse>
                    ) {
                        if (res.isSuccessful) {
                            getAllTodoStatus = TodoDataStatusUIState.Success(res.body()!!.data)
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )

                            getAllTodoStatus = TodoDataStatusUIState.Failed(errorMessage.errors)

                            if (res.code() == 401) {
                                viewModelScope.launch {
                                    GlobalUtil.resetUsernameToken(userRepository)
                                }

                                navController.navigate(PagesEnum.Login.name) {
                                    popUpTo(PagesEnum.Home.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetAllTodoResponse>, t: Throwable) {
                        getAllTodoStatus = TodoDataStatusUIState.Failed(t.localizedMessage)
                    }
                })
            } catch (error: IOException) {
                getAllTodoStatus = TodoDataStatusUIState.Failed(error.localizedMessage)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TodoListApplication)
                val userRepository = application.container.userRepository
                val todoRepository = application.container.todoRepository
                HomeViewModel(userRepository, todoRepository)
            }
        }
    }

    fun clearGetAllTodosErrorMessage() {
        getAllTodoStatus = TodoDataStatusUIState.Start
    }

    fun convertStringToEnum(text: String): PrioritiesEnum {
        if (text == "High") {
            return PrioritiesEnum.High
        } else if (text == "Medium") {
            return PrioritiesEnum.Medium
        } else {
            return PrioritiesEnum.Low
        }
    }
}