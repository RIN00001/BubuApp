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
import com.example.todolistapp.repositories.SavingRepositoryInterface
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.uiStates.SavingListFormUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.GeneralResponseModel
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.uiStates.StringDataStatusUIState
import com.example.todolistapp.utils.GlobalUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavingListFormViewModel(
    private val savingRepository: SavingRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {
    private val _savingListFormUIState = MutableStateFlow(SavingListFormUIState())

    val savingListFormUIState: StateFlow<SavingListFormUIState>
        get() {
            return _savingListFormUIState.asStateFlow()
        }

    val token: StateFlow<String> = userRepository.currentUserToken.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    var isUpdate by mutableStateOf(false)
        private set

    var savingId by mutableStateOf(-1)

    var submissionStatus: StringDataStatusUIState by mutableStateOf(StringDataStatusUIState.Start)
        private set

    var nameInput by mutableStateOf("")
        private set

    var targetAmountInput by mutableStateOf("")
        private set

    var currentAmountInput by mutableStateOf("")
        private set

    var dueDateInput by mutableStateOf("")
        private set

    var currentSavingModel: SavingModel? by mutableStateOf(null)

    fun changeNameInput(name: String) {
        if (name.length <= 50) {
            nameInput = name
        }
    }

    fun changeTargetAmountInput(targetAmount: String) {
        targetAmountInput = targetAmount
    }

    fun changeCurrentAmountInput(currentAmount: String) {
        currentAmountInput = currentAmount
    }

    fun showDatePickerDialog(datePickerDialog: DatePickerDialog) {
        datePickerDialog.show()
    }

    fun initDatePickerDialog(context: Context): DatePickerDialog {
        val datePickerCalendar = Calendar.getInstance()

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
        val nameOk = nameInput.trim().length >= 1
        val targetOk = targetAmountInput.toDoubleOrNull()?.let { it > 0 } ?: false
        val currentOk = currentAmountInput.isNotEmpty()
        val dateOk = dueDateInput.isNotEmpty()

        _savingListFormUIState.update { currentState ->
            currentState.copy(
                saveButtonEnabled = nameOk && targetOk && currentOk && dateOk
            )
        }
    }

    fun changeSaveButtonColor(): Color {
        if (_savingListFormUIState.value.saveButtonEnabled) {
            return Color.Blue
        }

        return Color.LightGray
    }

    private fun extractErrorMessage(res: Response<*>): String {
        return try {
            val raw = res.errorBody()?.string()
            if (raw.isNullOrBlank()) return "Unknown error"
            val json = Gson().fromJson(raw, JsonObject::class.java)
            json?.get("errors")?.asString
                ?: json?.get("error")?.asString
                ?: "Unknown error"
        } catch (_: Exception) {
            "Unknown error"
        }
    }

    private fun toIsoTimestamp(input: String): String {
        return try {
            val inFmt = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val parsed = inFmt.parse(input) ?: return input
            val outFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val day = outFmt.format(parsed)
            "${day}T00:00:00.000Z"
        } catch (_: ParseException) {
            input
        } catch (_: Exception) {
            input
        }
    }

    private fun isFormValid(): Boolean {
        val nameOk = nameInput.trim().length >= 1 && nameInput.trim().length <= 50
        val curr = currentAmountInput.toDoubleOrNull()
        val target = targetAmountInput.toDoubleOrNull() ?: -1.0
        val dateOk = dueDateInput.isNotBlank()

        return nameOk && curr != null && target > 0 && dateOk
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TodoListApplication)
                val savingRepository = application.container.savingRepository
                val userRepository = application.container.userRepository
                SavingListFormViewModel(savingRepository, userRepository)
            }
        }
    }

    fun createSaving(navController: NavHostController, token: String, userId: Int) {
        viewModelScope.launch {
            submissionStatus = StringDataStatusUIState.Loading

            try {
                if (!isFormValid()) {
                    submissionStatus = StringDataStatusUIState.Failed("Invalid form")
                    return@launch
                }

                val isoDate = toIsoTimestamp(dueDateInput)
                val call = savingRepository.createSaving(
                    token = token,
                    userId = userId,
                    id = 0,
                    amount = currentAmountInput.toDouble(),
                    targetamount = targetAmountInput.toDouble(),
                    name = nameInput.trim(),
                    goalDate = isoDate,
                    walletIds = emptyList()
                )

                call.enqueue(object: Callback<GeneralResponseModel> {
                    override fun onResponse(
                        call: Call<GeneralResponseModel>,
                        res: Response<GeneralResponseModel>
                    ) {
                        if (res.isSuccessful) {
                            submissionStatus = StringDataStatusUIState.Success(
                                res.body()?.data ?: "Saving created successfully"
                            )

                            resetViewModel()

                            navController.navigate(PagesEnum.Home.name) {
                                popUpTo(PagesEnum.CreateSaving.name) {
                                    inclusive = true
                                }
                            }
                        } else {
                            val message = extractErrorMessage(res)
                            submissionStatus = StringDataStatusUIState.Failed(message)

                            if (res.code() == 401) {
                                viewModelScope.launch {
                                    GlobalUtil.resetUsernameToken(userRepository)
                                }
                                navController.navigate(PagesEnum.Login.name) {
                                    popUpTo(PagesEnum.CreateSaving.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponseModel>, t: Throwable) {
                        submissionStatus = StringDataStatusUIState.Failed(t.localizedMessage ?: "Unknown error")
                    }
                })
            } catch (error: IOException) {
                submissionStatus = StringDataStatusUIState.Failed(error.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun updateSaving(token: String, userId: Int, navController: NavHostController) {
        viewModelScope.launch {
            submissionStatus = StringDataStatusUIState.Loading

            try {
                if (!isFormValid()) {
                    submissionStatus = StringDataStatusUIState.Failed("Invalid form")
                    return@launch
                }

                val isoDate = toIsoTimestamp(dueDateInput)
                val call = savingRepository.updateSaving(
                    token = token,
                    userId = userId,
                    id = savingId,
                    amount = currentAmountInput.toDouble(),
                    targetamount = targetAmountInput.toDouble(),
                    name = nameInput.trim(),
                    goalDate = isoDate,
                    walletIds = emptyList()
                )

                call.enqueue(object: Callback<GeneralResponseModel> {
                    override fun onResponse(
                        call: Call<GeneralResponseModel>,
                        res: Response<GeneralResponseModel>
                    ) {
                        if (res.isSuccessful) {
                            submissionStatus = StringDataStatusUIState.Success(
                                res.body()?.data ?: "Saving updated successfully"
                            )

                            SharedDataViewModel.editingSavingModel = null

                            resetViewModel()

                            navController.navigate(PagesEnum.Home.name) {
                                popUpTo(PagesEnum.EditSaving.name) {
                                    inclusive = true
                                }
                            }
                        } else {
                            val message = extractErrorMessage(res)
                            submissionStatus = StringDataStatusUIState.Failed(message)

                            if (res.code() == 401) {
                                viewModelScope.launch {
                                    GlobalUtil.resetUsernameToken(userRepository)
                                }
                                navController.navigate(PagesEnum.Login.name) {
                                    popUpTo(PagesEnum.EditSaving.name) { inclusive = true }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponseModel>, t: Throwable) {
                        submissionStatus = StringDataStatusUIState.Failed(t.localizedMessage ?: "Unknown error")
                    }
                })
            } catch (error: IOException) {
                submissionStatus = StringDataStatusUIState.Failed(error.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun navigateToUpdateForm(navController: NavHostController, savingModel: SavingModel) {
        SharedDataViewModel.editingSavingModel = savingModel

        nameInput = savingModel.name
        targetAmountInput = savingModel.targetamount.toString()
        currentAmountInput = savingModel.amount.toString()
        savingId = savingModel.id
        isUpdate = true

        try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val date = isoFormat.parse(savingModel.goalDate)
            dueDateInput = if (date != null) displayFormat.format(date) else ""
        } catch (_: Exception) {
            try {
                val altFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                val date = altFormat.parse(savingModel.goalDate)
                dueDateInput = if (date != null) displayFormat.format(date) else ""
            } catch (_: Exception) {
                dueDateInput = ""
            }
        }

        checkNullFormValues()

        navController.navigate(PagesEnum.EditSaving.name) {
            popUpTo(PagesEnum.Home.name) {
                inclusive = false
            }
        }
    }

    fun loadEditData(savingModel: SavingModel) {
        nameInput = savingModel.name
        targetAmountInput = savingModel.targetamount.toString()
        currentAmountInput = savingModel.amount.toString()
        savingId = savingModel.id
        isUpdate = true

        try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val date = isoFormat.parse(savingModel.goalDate)
            dueDateInput = if (date != null) displayFormat.format(date) else ""
        } catch (_: Exception) {
            try {
                val altFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                val date = altFormat.parse(savingModel.goalDate)
                dueDateInput = if (date != null) displayFormat.format(date) else ""
            } catch (_: Exception) {
                dueDateInput = ""
            }
        }

        checkNullFormValues()
    }

    fun addAmountToSaving(token: String, userId: Int, savingModel: SavingModel, amountToAdd: Double, navController: NavHostController, onSuccess: () -> Unit) {
        viewModelScope.launch {
            submissionStatus = StringDataStatusUIState.Loading

            try {
                android.util.Log.d("AddAmount", "Starting addAmountToSaving - userId: $userId, savingId: ${savingModel.id}, amount: $amountToAdd")

                val newAmount = savingModel.amount + amountToAdd
                android.util.Log.d("AddAmount", "New amount calculated: $newAmount (${savingModel.amount} + $amountToAdd)")

                val updateCall = savingRepository.updateSaving(
                    token = token,
                    userId = userId,
                    id = savingModel.id,
                    amount = newAmount,
                    targetamount = savingModel.targetamount,
                    name = savingModel.name,
                    goalDate = savingModel.goalDate,
                    walletIds = emptyList()
                )

                updateCall.enqueue(object: Callback<GeneralResponseModel> {
                    override fun onResponse(
                        call: Call<GeneralResponseModel>,
                        res: Response<GeneralResponseModel>
                    ) {
                        viewModelScope.launch {
                            android.util.Log.d("AddAmount", "UpdateSaving response - isSuccessful: ${res.isSuccessful}, code: ${res.code()}")

                            if (res.isSuccessful) {
                                submissionStatus = StringDataStatusUIState.Success("Amount added successfully")
                                currentSavingModel = null
                                onSuccess()
                                android.util.Log.d("AddAmount", "Success! Amount added")
                            } else {
                                val message = extractErrorMessage(res)
                                android.util.Log.e("AddAmount", "UpdateSaving failed: $message")
                                submissionStatus = StringDataStatusUIState.Failed(message)

                                if (res.code() == 401) {
                                    GlobalUtil.resetUsernameToken(userRepository)
                                    navController.navigate(PagesEnum.Login.name) {
                                        popUpTo(PagesEnum.Home.name) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponseModel>, t: Throwable) {
                        viewModelScope.launch {
                            android.util.Log.e("AddAmount", "UpdateSaving network error: ${t.message}", t)
                            submissionStatus = StringDataStatusUIState.Failed(t.localizedMessage ?: "Network error")
                        }
                    }
                })
            } catch (error: IOException) {
                android.util.Log.e("AddAmount", "IOException: ${error.message}", error)
                submissionStatus = StringDataStatusUIState.Failed(error.localizedMessage ?: "Unknown error")
            } catch (error: Exception) {
                android.util.Log.e("AddAmount", "Exception: ${error.message}", error)
                submissionStatus = StringDataStatusUIState.Failed(error.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun resetViewModel() {
        submissionStatus = StringDataStatusUIState.Start
        nameInput = ""
        dueDateInput = ""
        targetAmountInput = ""
        currentAmountInput = ""
        isUpdate = false
        savingId = -1
        currentSavingModel = null

        _savingListFormUIState.update { state ->
            state.copy(
                statusDropdownExpandedValue = false,
                priorityDropdownExpandedValue = false,
                showDatePickerDialog = false,
                saveButtonEnabled = false
            )
        }
    }

    fun clearErrorMessage() {
        submissionStatus = StringDataStatusUIState.Start
    }
}
