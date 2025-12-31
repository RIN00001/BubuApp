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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.AuthenticationStatusUIState
import com.example.todolistapp.viewModels.AuthenticationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    authenticationViewModel: AuthenticationViewModel,
    navController: NavHostController,
    context: Context
) {
    val uiState by authenticationViewModel.authenticationUIState.collectAsState()
    val status = authenticationViewModel.authenticationStatus

    LaunchedEffect(status) {
        when (status) {
            is AuthenticationStatusUIState.Success -> {
                navController.navigate(PagesEnum.Home.name) {
                    popUpTo(PagesEnum.Login.name) { inclusive = true }
                }
                authenticationViewModel.resetStatus()
            }
            is AuthenticationStatusUIState.Failed -> {
                Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))

            // --- INPUT EMAIL ---
            OutlinedTextField(
                value = authenticationViewModel.emailInput,
                onValueChange = {
                    authenticationViewModel.changeEmailInput(it)
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- INPUT PASSWORD ---
            OutlinedTextField(
                value = authenticationViewModel.passwordInput,
                onValueChange = {
                    authenticationViewModel.changePasswordInput(it)
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
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
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL LOGIN ---
            // Logic: Cek langsung apakah input kosong atau tidak
            val isFormValid = authenticationViewModel.emailInput.isNotEmpty() &&
                    authenticationViewModel.passwordInput.isNotEmpty()

            if (status is AuthenticationStatusUIState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { authenticationViewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isFormValid, // Aktif jika form valid
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        // Warna Biru jika valid, Abu-abu jika tidak
                        containerColor = if (isFormValid) Color.Blue else Color.LightGray
                    )
                ) {
                    Text(text = "Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- TOMBOL KE REGISTER ---
            TextButton(
                onClick = {
                    authenticationViewModel.resetViewModel()
                    navController.navigate(PagesEnum.Register.name)
                }
            ) {
                Text(text = "Don't have an account? Sign Up", color = Color.Gray)
            }
        }
    }
}