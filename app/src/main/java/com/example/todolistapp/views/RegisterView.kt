package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.uiStates.AuthenticationStatusUIState
import com.example.todolistapp.viewModels.AuthenticationViewModel
import com.example.todolistapp.views.templates.AuthenticationQuestion

@Composable
fun RegisterView(
    authenticationViewModel: AuthenticationViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context
) {
    val registerUIState by authenticationViewModel.authenticationUIState.collectAsState()
    val focusManager = LocalFocusManager.current

    // --- NAVIGASI ---
    val status = authenticationViewModel.authenticationStatus

    LaunchedEffect(status) {
        when (status) {
            is AuthenticationStatusUIState.Failed -> {
                Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
                authenticationViewModel.clearErrorMessage()
            }
            is AuthenticationStatusUIState.Success -> {
                Toast.makeText(context, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
                // REGISTER SUKSES -> PINDAH KE HALAMAN LOGIN
                navController.navigate(PagesEnum.Login.name) {
                    popUpTo(PagesEnum.Register.name) { inclusive = true }
                }
                authenticationViewModel.resetViewModel()
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Purple curved header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    color = Color(0xFF9B8FC7),
                    shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sign Up",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Form content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 320.dp)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Username field
            Text(
                text = "Username",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = authenticationViewModel.usernameInput,
                onValueChange = {
                    authenticationViewModel.changeUsernameInput(it)
                },
                placeholder = { Text("Lacrimosa", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            Text(
                text = "Email",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = authenticationViewModel.emailInput,
                onValueChange = {
                    authenticationViewModel.changeEmailInput(it)
                },
                placeholder = { Text("Lacrimosa@gmail.com", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(5.dp))

            // Password field
            Text(
                text = "Password",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = authenticationViewModel.passwordInput,
                onValueChange = {
                    authenticationViewModel.changePasswordInput(it)
                },
                placeholder = { Text("***********", color = Color.Gray) },
                visualTransformation = registerUIState.passwordVisibility,
                trailingIcon = {
                    IconButton(onClick = { authenticationViewModel.changePasswordVisibility() }) {
                        Icon(
                            painter = painterResource(id = registerUIState.passwordVisibilityIcon),
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Black,
                    focusedBorderColor = Color.Black,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (registerUIState.buttonEnabled) {
                            authenticationViewModel.register() // HAPUS navController
                        }
                    }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button
            Button(
                onClick = {
                    authenticationViewModel.register() // HAPUS navController
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9B8FC7),
                    disabledContainerColor = Color(0xFF9B8FC7).copy(alpha = 0.5f)
                ),
                enabled = registerUIState.buttonEnabled
            ) {
                if (status is AuthenticationStatusUIState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            // ... Sisa kode "Atau" dan "Guest Login" tetap sama ...
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Atau",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF9B8FC7),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Login sebagai tamu",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}