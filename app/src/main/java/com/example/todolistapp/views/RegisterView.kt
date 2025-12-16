package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.uiStates.AuthenticatonStatusUIState
import com.example.todolistapp.viewModels.AuthenticationViewModel

@Composable
fun RegisterView(
    authenticationViewModel: AuthenticationViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context
) {
    val registerUIState by authenticationViewModel.authenticationUIState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(authenticationViewModel.authenticationStatus) {
        val authenticationStatus = authenticationViewModel.authenticationStatus

        if (authenticationStatus is AuthenticatonStatusUIState.Failed) {
            Toast.makeText(context, authenticationStatus.errorMessage, Toast.LENGTH_SHORT).show()
            authenticationViewModel.clearErrorMessage()
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
                    authenticationViewModel.checkRegisterForm()
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
                    authenticationViewModel.checkRegisterForm()
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

            Spacer(modifier = Modifier.height(16.dp))

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
                    authenticationViewModel.checkRegisterForm()
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
                            authenticationViewModel.register(navController)
                        }
                    }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button
            Button(
                onClick = {
                    authenticationViewModel.register(navController)
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
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Atau" text
            Text(
                text = "Atau",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Guest login button
            OutlinedButton(
                onClick = {
                    // TODO: Implement guest login
                },
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

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun RegisterViewPreview() {
    TodoListAppTheme {
        RegisterView(
            modifier = Modifier.fillMaxSize(),
            authenticationViewModel = viewModel(),
            navController = rememberNavController(),
            context = LocalContext.current
        )
    }
}

