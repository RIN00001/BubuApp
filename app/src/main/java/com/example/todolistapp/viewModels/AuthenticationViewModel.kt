package com.example.todolistapp.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.R
import com.example.todolistapp.models.ErrorModel
import com.example.todolistapp.models.UserResponse
import com.example.todolistapp.repositories.AuthenticationRepositoryInterface
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.uiStates.AuthenticationStatusUIState
import com.example.todolistapp.uiStates.AuthenticationUIState
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    var emailInput by mutableStateOf("")
        private set
    var passwordInput by mutableStateOf("")
        private set

    var confirmPasswordInput by mutableStateOf("")
        private set

    // ==========================================================
    // --- INPUT HANDLERS (DIPISAH AGAR TIDAK KONFLIK) ---
    // ==========================================================

    // 1. KHUSUS UNTUK HALAMAN REGISTER (SIGN UP)
    fun changeUsernameInput(input: String) {
        usernameInput = input
        checkRegisterForm()
    }

    // Dipakai oleh Register View
    fun changeEmailInput(input: String) {
        emailInput = input
        checkRegisterForm()
    }

    // Dipakai oleh Register View
    fun changePasswordInput(input: String) {
        passwordInput = input
        checkRegisterForm()
    }

    fun changeConfirmPasswordInput(input: String) {
        confirmPasswordInput = input
        checkRegisterForm()
    }

    // 2. KHUSUS UNTUK HALAMAN LOGIN (BARU)
    fun changeLoginEmail(input: String) {
        emailInput = input
        checkLoginForm() // Hanya cek aturan login
    }

    fun changeLoginPassword(input: String) {
        passwordInput = input
        checkLoginForm() // Hanya cek aturan login
    }

    // ==========================================================

    // --- UI LOGIC ---
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
        // Login cuma butuh Email & Password
        val isValid = emailInput.isNotEmpty() && passwordInput.isNotEmpty()
        _authenticationUIState.update { it.copy(buttonEnabled = isValid) }
    }

    fun checkRegisterForm() {
        // Register butuh Username, Email, Password
        val isValid = emailInput.isNotEmpty() &&
                passwordInput.isNotEmpty() &&
                usernameInput.isNotEmpty()

        _authenticationUIState.update { it.copy(buttonEnabled = isValid) }
    }

    // --- RESET ---
    fun resetViewModel() {
        emailInput = ""
        passwordInput = ""
        usernameInput = ""
        confirmPasswordInput = ""
        authenticationStatus = AuthenticationStatusUIState.Start
        _authenticationUIState.update { AuthenticationUIState() }
    }

    fun clearErrorMessage() {
        if (authenticationStatus is AuthenticationStatusUIState.Failed) {
            authenticationStatus = AuthenticationStatusUIState.Start
        }
    }

    // ==========================================
    // --- API CALLS ---
    // ==========================================

    fun login() {
        authenticationStatus = AuthenticationStatusUIState.Loading
        val call = authenticationRepository.login(emailInput, passwordInput)

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data
                    val token = data?.token

                    if (token != null && data != null) {
                        saveUserSession(token, emailInput)
                        authenticationStatus = AuthenticationStatusUIState.Success(data)
                        Log.d("AuthViewModel", "Login Success: $token")
                    } else {
                        authenticationStatus = AuthenticationStatusUIState.Failed("Login berhasil tapi data/token kosong")
                    }
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody()?.string())
                    authenticationStatus = AuthenticationStatusUIState.Failed(errorMsg)
                    Log.e("AuthViewModel", "Login Error: $errorMsg")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                val msg = t.localizedMessage ?: "Network Error"
                authenticationStatus = AuthenticationStatusUIState.Failed(msg)
                Log.e("AuthViewModel", "Login Failure: $msg")
            }
        })
    }

    fun register() {
        authenticationStatus = AuthenticationStatusUIState.Loading
        val call = authenticationRepository.register(usernameInput, emailInput, passwordInput)

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data

                    if (data != null) {
                        authenticationStatus = AuthenticationStatusUIState.Success(data)
                        Log.d("AuthViewModel", "Register Success")
                    } else {
                        authenticationStatus = AuthenticationStatusUIState.Failed("Register berhasil tapi respon kosong")
                    }
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody()?.string())
                    authenticationStatus = AuthenticationStatusUIState.Failed(errorMsg)
                    Log.e("AuthViewModel", "Register Error: $errorMsg")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                val msg = t.localizedMessage ?: "Network Error"
                authenticationStatus = AuthenticationStatusUIState.Failed(msg)
                Log.e("AuthViewModel", "Register Failure: $msg")
            }
        })
    }

    // --- HELPER FUNCTIONS ---

    private fun saveUserSession(token: String, username: String) {
        viewModelScope.launch {
            userRepository.saveUserToken(token)
            userRepository.saveUsername(username)
        }
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            if (errorBody == null) return "Unknown Error"
            val errorResponse = Gson().fromJson(errorBody, ErrorModel::class.java)
            errorResponse.errors ?: "Request Failed"
        } catch (e: Exception) {
            "Gagal memproses error server"
        }
    }

    // --- FACTORY ---
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BubuApplication)
                AuthenticationViewModel(
                    authenticationRepository = application.container.authenticationRepository,
                    userRepository = application.container.userRepository
                )
            }
        }
    }
}