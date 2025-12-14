package com.example.todolistapp.viewModels

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.todolistapp.uiStates.AuthenticationUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.todolistapp.R
import com.example.todolistapp.uiStates.AuthenticatonStatusUIState
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.auth0.android.jwt.JWT
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.ErrorModel
import com.example.todolistapp.models.UserResponse
import com.example.todolistapp.repositories.AuthenticationRepository
import com.example.todolistapp.repositories.AuthenticationRepositoryInterface
import com.example.todolistapp.repositories.UserRepository
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AuthenticationViewModel(
    private val authenticationRepository: AuthenticationRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {
    var authenticationStatus: AuthenticatonStatusUIState by mutableStateOf(
        AuthenticatonStatusUIState.Start)
        private set

    private val _authenticationUIState = MutableStateFlow(AuthenticationUIState())

    val authenticationUIState: StateFlow<AuthenticationUIState>
        get() {
            return _authenticationUIState.asStateFlow()
        }

    var usernameInput by mutableStateOf("")
        private set

    var passwordInput by mutableStateOf("")
        private set

    var confirmPasswordInput by mutableStateOf("")
        private set

    var emailInput by mutableStateOf("")
        private set

    fun changeEmailInput(emailInput: String) {
        this.emailInput = emailInput
    }

    fun changeConfirmPasswordInput(confirmPasswordInput: String) {
        this.confirmPasswordInput = confirmPasswordInput
    }

    fun changeUsernameInput(usernameInput: String) {
        this.usernameInput = usernameInput
    }

    fun changePasswordInput(passwordInput: String) {
        this.passwordInput = passwordInput
    }

    fun changePasswordVisibility() {
        _authenticationUIState.update { currentState ->
            if (currentState.showPassword) {
                currentState.copy(
                    showPassword = false,
                    passwordVisibility = PasswordVisualTransformation(),
                    passwordVisibilityIcon = R.drawable.ic_password_visible
                )
            } else {
                currentState.copy(
                    showPassword = true,
                    passwordVisibility = VisualTransformation.None,
                    passwordVisibilityIcon = R.drawable.ic_password_invisible
                )
            }
        }
    }

    fun changeConfirmPasswordVisibility() {
        _authenticationUIState.update { currentState ->
            if (currentState.showConfirmPassword) {
                currentState.copy(
                    showConfirmPassword = false,
                    confirmPasswordVisibility = PasswordVisualTransformation(),
                    confirmPasswordVisibilityIcon = R.drawable.ic_password_visible
                )
            } else {
                currentState.copy(
                    showConfirmPassword = true,
                    confirmPasswordVisibility = VisualTransformation.None,
                    confirmPasswordVisibilityIcon = R.drawable.ic_password_invisible
                )
            }
        }
    }

    fun checkLoginForm() {
        if (emailInput.isNotEmpty() && passwordInput.isNotEmpty()) {
            _authenticationUIState.update { currentState ->
                currentState.copy(
                    buttonEnabled = true
                )
            }
        } else {
            _authenticationUIState.update { currentState ->
                currentState.copy(
                    buttonEnabled = false
                )
            }
        }
    }

    fun checkRegisterForm() {
        if (emailInput.isNotEmpty() && passwordInput.isNotEmpty() && usernameInput.isNotEmpty() && confirmPasswordInput.isNotEmpty() && passwordInput == confirmPasswordInput) {
            _authenticationUIState.update { currentState ->
                currentState.copy(
                    buttonEnabled = true
                )
            }
        } else {
            _authenticationUIState.update { currentState ->
                currentState.copy(
                    buttonEnabled = false
                )
            }
        }
    }

    fun checkButtonEnabled(isEnabled: Boolean): Color {
        if (isEnabled) {
            return Color.Blue
        }

        return Color.LightGray
    }

    fun resetViewModel() {
        changeEmailInput("")
        changePasswordInput("")
        changeUsernameInput("")
        changeConfirmPasswordInput("")
        _authenticationUIState.update { currentState ->
            currentState.copy(
                showConfirmPassword = false,
                showPassword = false,
                passwordVisibility = PasswordVisualTransformation(),
                confirmPasswordVisibility = PasswordVisualTransformation(),
                passwordVisibilityIcon = R.drawable.ic_password_visible,
                confirmPasswordVisibilityIcon = R.drawable.ic_password_visible,
                buttonEnabled = false
            )
        }
    }

    fun register(navController: NavHostController) {
        viewModelScope.launch {
            authenticationStatus = AuthenticatonStatusUIState.Loading

            try {
                val call = authenticationRepository.register(usernameInput, emailInput, passwordInput)

                call.enqueue(object: Callback<UserResponse>{
                    override fun onResponse(call: Call<UserResponse>, res: Response<UserResponse>) {
                        if (res.isSuccessful) {
                            val token = res.body()!!.data.token

                            val jwt = JWT(token!!)

                            val username = jwt.getClaim("username").asString()

                            saveUsernameToken(token, username!!)

                            authenticationStatus = AuthenticatonStatusUIState.Success(res.body()!!.data)

                            resetViewModel()

                            navController.navigate(PagesEnum.Home.name) {
                                popUpTo(PagesEnum.Login.name) {
                                    inclusive = true
                                }
                            }
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )

                            authenticationStatus = AuthenticatonStatusUIState.Failed(errorMessage.errors)
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        authenticationStatus = AuthenticatonStatusUIState.Failed(t.localizedMessage)
                    }
                })
            } catch (error: IOException) {
                authenticationStatus = AuthenticatonStatusUIState.Failed(error.localizedMessage)
            }
        }
    }

    fun login(navController: NavHostController) {
        viewModelScope.launch {
            authenticationStatus = AuthenticatonStatusUIState.Loading

            try {
                val call = authenticationRepository.login(emailInput, passwordInput)

                call.enqueue(object: Callback<UserResponse>{
                    override fun onResponse(call: Call<UserResponse>, res: Response<UserResponse>) {
                        if (res.isSuccessful) {
                            val token = res.body()!!.data.token

                            val jwt = JWT(token!!)

                            val username = jwt.getClaim("username").asString()

                            saveUsernameToken(token, username!!)

                            authenticationStatus = AuthenticatonStatusUIState.Success(res.body()!!.data)

                            resetViewModel()

                            navController.navigate(PagesEnum.Home.name) {
                                popUpTo(PagesEnum.Login.name) {
                                    inclusive = true
                                }
                            }
                        } else {
                            val errorMessage = Gson().fromJson(
                                res.errorBody()!!.charStream(),
                                ErrorModel::class.java
                            )

                            authenticationStatus = AuthenticatonStatusUIState.Failed(errorMessage.errors)
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        authenticationStatus = AuthenticatonStatusUIState.Failed(t.localizedMessage)
                    }
                })
            } catch (error: IOException) {
                authenticationStatus = AuthenticatonStatusUIState.Failed(error.localizedMessage)
            }
        }
    }

    fun saveUsernameToken(token: String, username: String) {
        viewModelScope.launch {
            userRepository.saveUserToken(token)
            userRepository.saveUsername(username)
        }
    }

    fun clearErrorMessage() {
        authenticationStatus = AuthenticatonStatusUIState.Start
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BubuApplication)
                val authenticationRepository = application.container.authenticationRepository
                val userRepository = application.container.userRepository
                AuthenticationViewModel(authenticationRepository, userRepository)
            }
        }
    }
}