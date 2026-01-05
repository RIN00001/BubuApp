package com.example.todolistapp.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.ErrorModel
import com.example.todolistapp.models.GeneralResponseModel
import com.example.todolistapp.models.GetSavingResponse
import com.example.todolistapp.repositories.SavingRepositoryInterface
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.uiStates.SavingDetailDataStatusUIState
import com.example.todolistapp.utils.GlobalUtil
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavingDetailViewModel(
    private val SavingRepository: SavingRepositoryInterface,
    private val UserRepository: UserRepositoryInterface
): ViewModel() {
    var getSavingStatus: SavingDetailDataStatusUIState by mutableStateOf(SavingDetailDataStatusUIState.Start)
        private set

    var deleteSavingStatus: SavingDetailDataStatusUIState by mutableStateOf(SavingDetailDataStatusUIState.Start)
        private set

    fun getSaving(token: String, userId: Int, id: Int, navController: NavHostController, isUpdating: Boolean) {
        viewModelScope.launch {
            getSavingStatus = SavingDetailDataStatusUIState.Loading

            try {
                val call = SavingRepository.getSaving(token = token, userId, id)

                call.enqueue(object: Callback<GetSavingResponse> {
                    override fun onResponse(
                        call: Call<GetSavingResponse>,
                        res: Response<GetSavingResponse>
                    ) {
                        if (res.isSuccessful) {
                            getSavingStatus = SavingDetailDataStatusUIState.Success(res.body()!!.data.toString())

                            if (isUpdating) {
                                navController.popBackStack()
                            } else {
                                navController.navigate(PagesEnum.SavingDetail.name) {
                                    popUpTo(PagesEnum.Books.name) {
                                        inclusive = false
                                    }
                                }
                            }
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )

                            getSavingStatus = SavingDetailDataStatusUIState.Failed(errorMessage.errors)

                            if (res.code() == 401) {
                                viewModelScope.launch {
                                    GlobalUtil.resetUsernameToken(UserRepository)
                                }

                                navController.navigate(PagesEnum.Login.name) {
                                    popUpTo(PagesEnum.Books.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GetSavingResponse>, t: Throwable) {
                        getSavingStatus = SavingDetailDataStatusUIState.Failed(t.localizedMessage ?: "Unknown error")
                    }
                })
            } catch (error: IOException) {
                getSavingStatus = SavingDetailDataStatusUIState.Failed(error.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun deleteSaving(token: String, userId: Int, id: Int, navController: NavHostController) {
        viewModelScope.launch {
            deleteSavingStatus = SavingDetailDataStatusUIState.Loading

            try {
                val call = SavingRepository.deleteSaving(token = token, userId, id)

                call.enqueue(object: Callback<GeneralResponseModel> {
                    override fun onResponse(
                        call: Call<GeneralResponseModel>,
                        res: Response<GeneralResponseModel>
                    ) {
                        if (res.isSuccessful) {
                            deleteSavingStatus = SavingDetailDataStatusUIState.Success(
                                res.body()?.data ?: "Saving deleted successfully"
                            )

                            navController.popBackStack()
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )

                            deleteSavingStatus = SavingDetailDataStatusUIState.Failed(errorMessage.errors)

                            if (res.code() == 401) {
                                viewModelScope.launch {
                                    GlobalUtil.resetUsernameToken(UserRepository)
                                }

                                navController.navigate(PagesEnum.Login.name) {
                                    popUpTo(PagesEnum.SavingDetail.name) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeneralResponseModel>, t: Throwable) {
                        deleteSavingStatus = SavingDetailDataStatusUIState.Failed(t.localizedMessage ?: "Unknown error")
                    }
                })
            } catch (error: IOException) {
                deleteSavingStatus = SavingDetailDataStatusUIState.Failed(error.localizedMessage ?: "Unknown error")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BubuApplication)
                val savingRepository = application.container.savingRepository
                val userRepository = application.container.userRepository

                SavingDetailViewModel(savingRepository, userRepository)
            }
        }
    }
}
