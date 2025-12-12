package com.example.todolistapp.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.todolistapp.TodoListApplication
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.ErrorModel
import com.example.todolistapp.models.GeneralResponseModel
import com.example.todolistapp.models.GetAllTodoResponse
import com.example.todolistapp.models.GetTodoResponse
import com.example.todolistapp.repositories.TodoRepositoryInterface
import com.example.todolistapp.uiStates.StringDataStatusUIState
import com.example.todolistapp.uiStates.TodoDataStatusUIState
import com.example.todolistapp.uiStates.TodoDetailDataStatusUIState
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.utils.GlobalUtil


class TodoDetailViewModel (
    private val todoRepository: TodoRepositoryInterface,
    private val userRepository: UserRepositoryInterface
): ViewModel() {
    var getTodoStatus: TodoDetailDataStatusUIState by mutableStateOf(TodoDetailDataStatusUIState.Start)
        private set

    var deleteTodoStatus: StringDataStatusUIState by mutableStateOf(StringDataStatusUIState.Start)
        private set

    fun getTodo(token: String, todoId: Int, navController: NavHostController, isUpdating: Boolean) {
        viewModelScope.launch {
            getTodoStatus = TodoDetailDataStatusUIState.Loading

            try {
                val call = todoRepository.getTodo(token = token, todoId)

                call.enqueue(object: Callback<GetTodoResponse> {
                    override fun onResponse(
                        call: Call<GetTodoResponse>,
                        res: Response<GetTodoResponse>
                    ) {
                        if (res.isSuccessful) {
                            getTodoStatus = TodoDetailDataStatusUIState.Success(res.body()!!.data)

                            if (isUpdating) {
                                navController.popBackStack()
                            } else {
                                navController.navigate(PagesEnum.TodoDetail.name) {
                                    popUpTo(PagesEnum.Home.name) {
                                        inclusive = false
                                    }
                                }
                            }
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )

                            getTodoStatus = TodoDetailDataStatusUIState.Failed(errorMessage.errors)

                            if (res.code() == 401) {
                                viewModelScope.launch {
                                    GlobalUtil.resetUsernameToken(userRepository)
                                }

                                navController.navigate(PagesEnum.Login.name) {
                                    popUpTo(PagesEnum.TodoDetail.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetTodoResponse>, t: Throwable) {
                        getTodoStatus = TodoDetailDataStatusUIState.Failed(t.localizedMessage)
                    }
                })
            } catch (error: IOException) {
                getTodoStatus = TodoDetailDataStatusUIState.Failed(error.localizedMessage)
            }
        }
    }

    fun deleteTodo(token: String, todoId: Int, navController: NavHostController) {
        viewModelScope.launch {
            deleteTodoStatus = StringDataStatusUIState.Loading

            try {
                val call = todoRepository.deleteTodo(token = token, todoId)

                call.enqueue(object: Callback<GeneralResponseModel> {
                    override fun onResponse(
                        call: Call<GeneralResponseModel>,
                        res: Response<GeneralResponseModel>
                    ) {
                        if (res.isSuccessful) {
                            deleteTodoStatus = StringDataStatusUIState.Success(res.body()!!.data)

                            navController.popBackStack()
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )

                            deleteTodoStatus = StringDataStatusUIState.Failed(errorMessage.errors)

                            if (res.code() == 401) {
                                viewModelScope.launch {
                                    GlobalUtil.resetUsernameToken(userRepository)
                                }

                                navController.navigate(PagesEnum.Login.name) {
                                    popUpTo(PagesEnum.TodoDetail.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponseModel>, t: Throwable) {
                        deleteTodoStatus = StringDataStatusUIState.Failed(t.localizedMessage)
                    }
                })
            } catch (error: IOException) {
                deleteTodoStatus = StringDataStatusUIState.Failed(error.localizedMessage)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TodoListApplication)
                val todoRepository = application.container.todoRepository
                val userRepository = application.container.userRepository

                TodoDetailViewModel(todoRepository, userRepository)
            }
        }
    }

    fun clearDeleteTodoErrorMessage() {
        deleteTodoStatus = StringDataStatusUIState.Start
    }
}