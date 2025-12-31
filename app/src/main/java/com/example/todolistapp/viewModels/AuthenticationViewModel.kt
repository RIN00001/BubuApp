package com.example.todolistapp.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.auth0.android.jwt.JWT
import com.example.todolistapp.R
import com.example.todolistapp.TodoListApplication
import com.example.todolistapp.models.ErrorModel
import com.example.todolistapp.models.UserResponse
import com.example.todolistapp.repositories.AuthenticationRepositoryInterface
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.utils.TokenManager // <--- PENTING: Import TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

// Pastikan file AuthenticationStatusUIState.kt dan AuthenticationUIState.kt sudah ada
// Jika belum, definisikan sealed interface di file terpisah atau di atas class ini
import com.example.todolistapp.uiStates.AuthenticationStatusUIState
import com.example.todolistapp.uiStates.AuthenticationUIState

class AuthenticationViewModel(
    private val authenticationRepository: AuthenticationRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    // --- STATE ---
    var authenticationStatus: AuthenticationStatusUIState by mutableStateOf(AuthenticationStatusUIState.Start)
        private set

    private val _authenticationUIState = MutableStateFlow(AuthenticationUIState())
    val authenticationUIState: StateFlow<AuthenticationUIState> = _authenticationUIState.asStateFlow()

    // --- INPUT VARIABLES ---
    var usernameInput by mutableStateOf("")
        private set
    var passwordInput by mutableStateOf("")
        private set
    var confirmPasswordInput by mutableStateOf("")
        private set
    var emailInput by mutableStateOf("")
        private set

    // --- INPUT HANDLERS ---
    fun changeEmailInput(input: String) { emailInput = input }
    fun changeUsernameInput(input: String) { usernameInput = input }
    fun changePasswordInput(input: String) { passwordInput = input }
    fun changeConfirmPasswordInput(input: String) { confirmPasswordInput = input }

    // --- VISIBILITY & FORM CHECKERS ---
    fun changePasswordVisibility() {
        _authenticationUIState.update { currentState ->
            if (currentState.showPassword) {
                currentState.copy(showPassword = false, passwordVisibility = PasswordVisualTransformation(), passwordVisibilityIcon = R.drawable.ic_password_visible)
            } else {
                currentState.copy(showPassword = true, passwordVisibility = VisualTransformation.None, passwordVisibilityIcon = R.drawable.ic_password_invisible)
            }
        }
    }

    fun changeConfirmPasswordVisibility() {
        _authenticationUIState.update { currentState ->
            if (currentState.showConfirmPassword) {
                currentState.copy(showConfirmPassword = false, confirmPasswordVisibility = PasswordVisualTransformation(), confirmPasswordVisibilityIcon = R.drawable.ic_password_visible)
            } else {
                currentState.copy(showConfirmPassword = true, confirmPasswordVisibility = VisualTransformation.None, confirmPasswordVisibilityIcon = R.drawable.ic_password_invisible)
            }
        }
    }

    fun checkLoginForm() {
        if (emailInput.isNotEmpty() && passwordInput.isNotEmpty()) {
            _authenticationUIState.update { it.copy(buttonEnabled = true) }
        } else {
            _authenticationUIState.update { it.copy(buttonEnabled = false) }
        }
    }

    fun checkRegisterForm() {
        if (emailInput.isNotEmpty() && passwordInput.isNotEmpty() && usernameInput.isNotEmpty() && confirmPasswordInput.isNotEmpty() && passwordInput == confirmPasswordInput) {
            _authenticationUIState.update { it.copy(buttonEnabled = true) }
        } else {
            _authenticationUIState.update { it.copy(buttonEnabled = false) }
        }
    }

    fun checkButtonEnabled(isEnabled: Boolean): Color {
        return if (isEnabled) Color.Blue else Color.LightGray
    }

    fun resetViewModel() {
        changeEmailInput("")
        changePasswordInput("")
        changeUsernameInput("")
        changeConfirmPasswordInput("")
        _authenticationUIState.update {
            it.copy(
                showConfirmPassword = false, showPassword = false,
                passwordVisibility = PasswordVisualTransformation(), confirmPasswordVisibility = PasswordVisualTransformation(),
                buttonEnabled = false
            )
        }
        authenticationStatus = AuthenticationStatusUIState.Start
    }

    // --- FUNGSI LOGIN ---
    fun login() {
        viewModelScope.launch {
            authenticationStatus = AuthenticationStatusUIState.Loading

            try {
                val call = authenticationRepository.login(emailInput, passwordInput)

                call.enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, res: Response<UserResponse>) {
                        if (res.isSuccessful) {
                            val data = res.body()?.data
                            val token = data?.token

                            if (token != null && data != null) {

                                val jwt = JWT(token)
                                val username = jwt.getClaim("username").asString() ?: "User"

                                TokenManager.saveToken(token)

                                saveUsernameToken(token, username)

                                authenticationStatus = AuthenticationStatusUIState.Success(data)
                            } else {
                                authenticationStatus = AuthenticationStatusUIState.Failed("Token Kosong dari Server")
                            }
                        } else {
                            try {
                                val errorBody = res.errorBody()?.string()
                                val errorMessage = try {
                                    Gson().fromJson(errorBody, ErrorModel::class.java).errors
                                } catch (e: Exception) {
                                    "Login Gagal: ${res.code()}"
                                }
                                authenticationStatus = AuthenticationStatusUIState.Failed(errorMessage)
                            } catch (e: Exception) {
                                authenticationStatus = AuthenticationStatusUIState.Failed("Login Gagal: ${res.code()}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        authenticationStatus = AuthenticationStatusUIState.Failed(t.localizedMessage ?: "Unknown Error")
                    }
                })
            } catch (error: IOException) {
                authenticationStatus = AuthenticationStatusUIState.Failed(error.localizedMessage ?: "Network Error")
            }
        }
    }

    // --- FUNGSI REGISTER ---
    // Diperbarui agar konsisten: Tidak perlu parameter NavController
    // Biarkan View yang menangani navigasi saat status == Success
    fun register() {
        viewModelScope.launch {
            authenticationStatus = AuthenticationStatusUIState.Loading
            try {
                val call = authenticationRepository.register(usernameInput, emailInput, passwordInput)
                call.enqueue(object: Callback<UserResponse>{
                    override fun onResponse(call: Call<UserResponse>, res: Response<UserResponse>) {
                        if (res.isSuccessful) {
                            val data = res.body()?.data
                            val token = data?.token

                            if (token != null && data != null) {
                                val jwt = JWT(token)
                                val username = jwt.getClaim("username").asString() ?: "User"

                                // SIMPAN TOKEN (PENTING)
                                TokenManager.saveToken(token)
                                saveUsernameToken(token, username)

                                // Update State -> View akan pindah ke Home
                                authenticationStatus = AuthenticationStatusUIState.Success(data)

                                // Reset form setelah sukses
                                resetViewModel()
                            }
                        } else {
                            try {
                                val errorBody = res.errorBody()?.string()
                                val errorMessage = try {
                                    Gson().fromJson(errorBody, ErrorModel::class.java).errors
                                } catch (e: Exception) {
                                    "Register Gagal: ${res.code()}"
                                }
                                authenticationStatus = AuthenticationStatusUIState.Failed(errorMessage)
                            } catch (e: Exception) {
                                authenticationStatus = AuthenticationStatusUIState.Failed("Register Gagal")
                            }
                        }
                    }
                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        authenticationStatus = AuthenticationStatusUIState.Failed(t.localizedMessage ?: "Unknown Error")
                    }
                })
            } catch (error: IOException) {
                authenticationStatus = AuthenticationStatusUIState.Failed(error.localizedMessage ?: "Network Error")
            }
        }
    }

    // --- HELPER FUNCTIONS ---
    private fun saveUsernameToken(token: String, username: String) {
        viewModelScope.launch {
            // Kita simpan juga di UserRepo (DataStore) sebagai cadangan/persistence
            userRepository.saveUserToken(token)
            userRepository.saveUsername(username)
        }
    }

    fun resetStatus() {
        authenticationStatus = AuthenticationStatusUIState.Start
    }

    fun clearErrorMessage() {
        authenticationStatus = AuthenticationStatusUIState.Start
    }

    // --- FACTORY (Dependency Injection) ---
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TodoListApplication)
                AuthenticationViewModel(
                    authenticationRepository = application.container.authenticationRepository,
                    userRepository = application.container.userRepository
                )
            }
        }
    }
}