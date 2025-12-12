package com.example.todolistapp.viewModels

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
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
import com.example.todolistapp.repositories.TodoRepository
import com.example.todolistapp.repositories.TodoRepositoryInterface
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.uiStates.TodoListFormUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Date
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.navigation.NavController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.ErrorModel
import com.example.todolistapp.models.GeneralResponseModel
import com.example.todolistapp.models.TodoModel
import com.example.todolistapp.uiStates.StringDataStatusUIState
import com.example.todolistapp.utils.GlobalUtil
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoListFormViewModel(
    private val todoRepository: TodoRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {
    private val _todoListFormUIState = MutableStateFlow(TodoListFormUIState())

    val todoListFormUIState: StateFlow<TodoListFormUIState>
        get() {
            return _todoListFormUIState.asStateFlow()
        }

    val token: StateFlow<String> = userRepository.currentUserToken.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    var isUpdate by mutableStateOf(false)
        private set

    var todoId by mutableStateOf(-1)
        private set

    var submissionStatus: StringDataStatusUIState by mutableStateOf(StringDataStatusUIState.Start)
        private set

    var titleInput by mutableStateOf("")
        private set

    var descriptionInput by mutableStateOf("")
        private set

    var statusInput by mutableStateOf("")
        private set

    var priorityInput by mutableStateOf("")
        private set

    var dueDateInput by mutableStateOf("")
        private set

    fun changeTitleInput(title: String) {
        titleInput = title
    }

    fun changeDescriptionInput(description: String) {
        descriptionInput = description
    }

    fun changeStatusInput(status: String) {
        statusInput = status
    }

    fun changePriorityInput(priority: String) {
        priorityInput = priority
    }

    fun changeStatusExpandedValue(
        expanded: Boolean
    ) {
        _todoListFormUIState.update { currentState ->
            currentState.copy(
                statusDropdownExpandedValue = expanded
            )
        }
    }

    fun dismissStatusDropdownMenu() {
        _todoListFormUIState.update { currentState ->
            currentState.copy(
                statusDropdownExpandedValue = false
            )
        }
    }

    fun changePriorityExpandedValue(
        expanded: Boolean
    ) {
        _todoListFormUIState.update { currentState ->
            currentState.copy(
                priorityDropdownExpandedValue = expanded
            )
        }
    }

    fun dismissPriorityDropdownMenu() {
        _todoListFormUIState.update { currentState ->
            currentState.copy(
                priorityDropdownExpandedValue = false
            )
        }
    }

    fun showDatePickerDialog(datePickerDialog: DatePickerDialog) {
        datePickerDialog.show()
    }

    fun initDatePickerDialog(context: Context): DatePickerDialog {
        // Initializing a Calendar
        val datePickerCalendar = Calendar.getInstance()

        // Fetching current year, month and day
        val calYear = datePickerCalendar.get(Calendar.YEAR)
        val calMonth = datePickerCalendar.get(Calendar.MONTH)
        val calDay = datePickerCalendar.get(Calendar.DAY_OF_MONTH)

        datePickerCalendar.time = Date()

        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, calYear: Int, calMonth: Int, calDay: Int ->
                dueDateInput = "$calDay/${calMonth + 1}/$calYear"
                checkNullFormValues()
            }, calYear, calMonth, calDay
        )

        return datePickerDialog
    }

    fun checkNullFormValues() {
        if (titleInput.isNotEmpty() && descriptionInput.isNotEmpty() && statusInput.isNotEmpty() && priorityInput.isNotEmpty() && dueDateInput.isNotEmpty()) {
            _todoListFormUIState.update { currentState ->
                currentState.copy(
                    saveButtonEnabled = true
                )
            }
        } else {
            _todoListFormUIState.update { currentState ->
                currentState.copy(
                    saveButtonEnabled = false
                )
            }
        }
    }

    fun changeSaveButtonColor(): Color {
        if (_todoListFormUIState.value.saveButtonEnabled) {
            return Color.Blue
        }

        return Color.LightGray
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TodoListApplication)
                val todoRepository = application.container.todoRepository
                val userRepository = application.container.userRepository
                TodoListFormViewModel(todoRepository, userRepository)
            }
        }
    }

    fun createTodo(navController: NavHostController, token: String) {
        viewModelScope.launch {
            submissionStatus = StringDataStatusUIState.Loading

            try {
                val call = todoRepository.createTodo(token = token, title = titleInput, status = statusInput, description = descriptionInput, dueDate = dueDateInput, priority = priorityInput)

                call.enqueue(object: Callback<GeneralResponseModel> {
                    override fun onResponse(
                        call: Call<GeneralResponseModel>,
                        res: Response<GeneralResponseModel>
                    ) {
                        if (res.isSuccessful) {
                            submissionStatus = StringDataStatusUIState.Success(res.body()!!.data)

                            resetViewModel()

                            navController.navigate(PagesEnum.Home.name) {
                                popUpTo(PagesEnum.CreateTodo.name) {
                                    inclusive = true
                                }
                            }
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )

                            submissionStatus = StringDataStatusUIState.Failed(errorMessage.errors)

                            if (res.code() == 401) {
                                viewModelScope.launch {
                                    GlobalUtil.resetUsernameToken(userRepository)
                                }

                                navController.navigate(PagesEnum.Login.name) {
                                    popUpTo(PagesEnum.CreateTodo.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponseModel?>, t: Throwable) {
                        submissionStatus = StringDataStatusUIState.Failed(t.localizedMessage)
                    }
                })
            } catch (error: IOException) {
                submissionStatus = StringDataStatusUIState.Failed(error.localizedMessage)
            }
        }
    }

    fun updateTodo(token: String, getTodo: () -> Unit, navController: NavHostController) {
        viewModelScope.launch {
            submissionStatus = StringDataStatusUIState.Loading

            try {
                val call = todoRepository.updateTodo(token = token, todoId, title = titleInput, status = statusInput, description = descriptionInput, dueDate = dueDateInput, priority = priorityInput)

                call.enqueue(object: Callback<GeneralResponseModel> {
                    override fun onResponse(
                        call: Call<GeneralResponseModel>,
                        res: Response<GeneralResponseModel>
                    ) {
                        if (res.isSuccessful) {
                            submissionStatus = StringDataStatusUIState.Success(res.body()!!.data)

                            resetViewModel()

                            getTodo()
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )

                            submissionStatus = StringDataStatusUIState.Failed(errorMessage.errors)

                            if (res.code() == 401) {
                                viewModelScope.launch {
                                    GlobalUtil.resetUsernameToken(userRepository)
                                }

                                navController.navigate(PagesEnum.Login.name) {
                                    popUpTo(PagesEnum.CreateTodo.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponseModel?>, t: Throwable) {
                        submissionStatus = StringDataStatusUIState.Failed(t.localizedMessage)
                    }
                })
            } catch (error: IOException) {
                submissionStatus = StringDataStatusUIState.Failed(error.localizedMessage)
            }
        }
    }

    fun navigateToUpdateForm(navController: NavHostController, todoModel: TodoModel) {
        titleInput = todoModel.title
        descriptionInput = todoModel.description
        dueDateInput = todoModel.dueDate
        statusInput = todoModel.status
        priorityInput = todoModel.priority
        todoId = todoModel.id

        isUpdate = true

        navController.navigate(PagesEnum.CreateTodo.name) {
            popUpTo(PagesEnum.TodoDetail.name) {
                inclusive = false
            }
        }
    }

    fun resetViewModel() {
        submissionStatus = StringDataStatusUIState.Start
        titleInput = ""
        descriptionInput = ""
        dueDateInput = ""
        statusInput = ""
        priorityInput = ""
        isUpdate = false

        _todoListFormUIState.update { state ->
            state.copy(
                false,
                false,
                showDatePickerDialog = false,
                saveButtonEnabled = false
            )
        }
    }

    fun clearErrorMessage() {
        submissionStatus = StringDataStatusUIState.Start
    }
}