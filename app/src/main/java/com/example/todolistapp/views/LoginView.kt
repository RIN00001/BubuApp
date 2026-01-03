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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.todolistapp.views.templates.AuthenticationQuestion

@Composable
fun LoginView(
    authenticationViewModel: AuthenticationViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context
) {
    val loginUIState by authenticationViewModel.authenticationUIState.collectAsState()
    val focusManager = LocalFocusManager.current
    val status = authenticationViewModel.authenticationStatus

    LaunchedEffect(status) {
        when (status) {
            is AuthenticationStatusUIState.Failed -> {
                Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
                authenticationViewModel.clearErrorMessage()
            }
            is AuthenticationStatusUIState.Success -> {
                // --- PERBAIKAN DI SINI ---
                // Menggunakan PagesEnum.Books.name agar sesuai dengan enum kamu
                navController.navigate(PagesEnum.Books.name) {
                    popUpTo(PagesEnum.Login.name) { inclusive = true }
                }
                authenticationViewModel.resetViewModel()
            }
            else -> {}
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Header
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
                text = "Login",
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
            // Email field
            Text("Email", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = authenticationViewModel.emailInput,
                onValueChange = {
                    // Menggunakan fungsi khusus Login agar tidak bentrok dengan validasi Register
                    authenticationViewModel.changeLoginEmail(it)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Password field
            Text("Password", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = authenticationViewModel.passwordInput,
                onValueChange = {
                    // Menggunakan fungsi khusus Login
                    authenticationViewModel.changeLoginPassword(it)
                },
                placeholder = { Text("***********", color = Color.Gray) },
                visualTransformation = loginUIState.passwordVisibility,
                trailingIcon = {
                    IconButton(onClick = { authenticationViewModel.changePasswordVisibility() }) {
                        Icon(painter = painterResource(id = loginUIState.passwordVisibilityIcon), contentDescription = null)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (loginUIState.buttonEnabled) authenticationViewModel.login()
                }),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Continue button
            Button(
                onClick = { authenticationViewModel.login() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9B8FC7),
                    disabledContainerColor = Color(0xFF9B8FC7).copy(alpha = 0.5f)
                ),
                enabled = loginUIState.buttonEnabled
            ) {
                if (status is AuthenticationStatusUIState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign up link
            AuthenticationQuestion(
                questionText = stringResource(id = R.string.don_t_have_an_account_yet_text),
                actionText = stringResource(id = R.string.sign_up_text),
                onActionTextClicked = {
                    authenticationViewModel.resetViewModel()
                    navController.navigate(PagesEnum.Register.name) {
                        popUpTo(PagesEnum.Login.name) { inclusive = true }
                    }
                },
                rowModifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}