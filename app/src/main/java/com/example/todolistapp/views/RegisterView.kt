package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolistapp.R
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.AuthenticationStatusUIState
import com.example.todolistapp.viewModels.AuthenticationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    authenticationViewModel: AuthenticationViewModel,
    navController: NavHostController,
    context: Context
) {
    // 1. Ambil State UI dan Status
    val uiState by authenticationViewModel.authenticationUIState.collectAsState()
    val status = authenticationViewModel.authenticationStatus

    // 2. LISTENER STATUS: Menangani Navigasi & Error Toast
    LaunchedEffect(status) {
        when (status) {
            is AuthenticationStatusUIState.Success -> {
                // Jika sukses, pindah ke Home & hapus history login/register
                navController.navigate(PagesEnum.Home.name) {
                    popUpTo(PagesEnum.Login.name) { inclusive = true }
                }
                authenticationViewModel.resetStatus()
            }
            is AuthenticationStatusUIState.Failed -> {
                // Jika gagal, tampilkan Toast
                Toast.makeText(context, status.errorMessage, Toast.LENGTH_LONG).show()
                authenticationViewModel.clearErrorMessage()
            }
            else -> {}
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- HEADER ---
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign up to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- INPUT USERNAME ---
            OutlinedTextField(
                value = authenticationViewModel.usernameInput,
                onValueChange = {
                    authenticationViewModel.changeUsernameInput(it)
                    authenticationViewModel.checkRegisterForm()
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT EMAIL ---
            OutlinedTextField(
                value = authenticationViewModel.emailInput,
                onValueChange = {
                    authenticationViewModel.changeEmailInput(it)
                    authenticationViewModel.checkRegisterForm()
                },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT PASSWORD ---
            OutlinedTextField(
                value = authenticationViewModel.passwordInput,
                onValueChange = {
                    authenticationViewModel.changePasswordInput(it)
                    authenticationViewModel.checkRegisterForm()
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = uiState.passwordVisibility,
                trailingIcon = {
                    IconButton(onClick = { authenticationViewModel.changePasswordVisibility() }) {
                        Image(
                            painter = painterResource(id = uiState.passwordVisibilityIcon),
                            contentDescription = "Toggle Password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT CONFIRM PASSWORD ---
            OutlinedTextField(
                value = authenticationViewModel.confirmPasswordInput,
                onValueChange = {
                    authenticationViewModel.changeConfirmPasswordInput(it)
                    authenticationViewModel.checkRegisterForm()
                },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = uiState.confirmPasswordVisibility,
                trailingIcon = {
                    IconButton(onClick = { authenticationViewModel.changeConfirmPasswordVisibility() }) {
                        Image(
                            painter = painterResource(id = uiState.confirmPasswordVisibilityIcon),
                            contentDescription = "Toggle Confirm Password",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- BUTTON REGISTER ---
            if (status is AuthenticationStatusUIState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        // FIX: Panggil register() TANPA parameter NavController
                        authenticationViewModel.register()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = uiState.buttonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = authenticationViewModel.checkButtonEnabled(uiState.buttonEnabled)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LINK KE LOGIN ---
            TextButton(
                onClick = {
                    navController.navigate(PagesEnum.Login.name) {
                        popUpTo(PagesEnum.Login.name) { inclusive = true }
                    }
                }
            ) {
                Text(
                    text = "Already have an account? Login",
                    color = Color.Gray
                )
            }
        }
    }
}