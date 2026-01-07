package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.AuthenticationStatusUIState
import com.example.todolistapp.viewModels.AuthenticationViewModel

enum class RegisterStep {
    EMAIL, USERNAME, PASSWORD, SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    authenticationViewModel: AuthenticationViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context
) {
    var currentStep by remember { mutableStateOf(RegisterStep.EMAIL) }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Password validation states
    val hasMinLength = password.length >= 8
    val hasNumber = password.any { it.isDigit() }
    val hasSymbol = password.any { !it.isLetterOrDigit() }
    val allValid = hasMinLength && hasNumber && hasSymbol

    // Password strength
    val passwordStrength = when {
        password.isEmpty() -> 0f
        hasMinLength && !hasNumber && !hasSymbol -> 0.33f
        hasMinLength && (hasNumber || hasSymbol) -> 0.66f
        allValid -> 1f
        else -> 0.2f
    }

    val strengthColor = when {
        passwordStrength <= 0.33f -> Color.Red
        passwordStrength <= 0.66f -> Color(0xFFFFA500) // Orange
        else -> Color(0xFFFFC107) // Yellow/Gold
    }

    val status = authenticationViewModel.authenticationStatus

    LaunchedEffect(status) {
        when (status) {
            is AuthenticationStatusUIState.Failed -> {
                Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
                authenticationViewModel.clearErrorMessage()
            }
            is AuthenticationStatusUIState.Success -> {
                currentStep = RegisterStep.SUCCESS
                authenticationViewModel.resetViewModel()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            if (currentStep != RegisterStep.SUCCESS) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentStep) {
                                RegisterStep.EMAIL -> "Add your email 1 / 3"
                                RegisterStep.USERNAME -> "Create your username 2 / 3"
                                RegisterStep.PASSWORD -> "Create your password 3 / 3"
                                else -> ""
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when (currentStep) {
                                RegisterStep.EMAIL -> navController.popBackStack()
                                RegisterStep.USERNAME -> currentStep = RegisterStep.EMAIL
                                RegisterStep.PASSWORD -> currentStep = RegisterStep.USERNAME
                                else -> {}
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            when (currentStep) {
                RegisterStep.EMAIL -> {
                    EmailStep(
                        email = email,
                        onEmailChange = { email = it },
                        onContinue = {
                            if (email.isNotBlank()) {
                                authenticationViewModel.changeEmailInput(email)
                                currentStep = RegisterStep.USERNAME
                            }
                        },
                        isLoading = status is AuthenticationStatusUIState.Loading
                    )
                }
                RegisterStep.USERNAME -> {
                    UsernameStep(
                        username = username,
                        onUsernameChange = { username = it },
                        onContinue = {
                            if (username.isNotBlank()) {
                                authenticationViewModel.changeUsernameInput(username)
                                currentStep = RegisterStep.PASSWORD
                            }
                        },
                        isLoading = status is AuthenticationStatusUIState.Loading
                    )
                }
                RegisterStep.PASSWORD -> {
                    PasswordStep(
                        password = password,
                        onPasswordChange = { password = it },
                        passwordVisible = passwordVisible,
                        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                        hasMinLength = hasMinLength,
                        hasNumber = hasNumber,
                        hasSymbol = hasSymbol,
                        passwordStrength = passwordStrength,
                        strengthColor = strengthColor,
                        onContinue = {
                            if (allValid) {
                                authenticationViewModel.changePasswordInput(password)
                                authenticationViewModel.register()
                            }
                        },
                        isLoading = status is AuthenticationStatusUIState.Loading,
                        continueEnabled = allValid
                    )
                }
                RegisterStep.SUCCESS -> {
                    SuccessStep(
                        onLoginClick = {
                            navController.navigate(PagesEnum.Login.name) {
                                popUpTo(PagesEnum.Register.name) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmailStep(
    email: String,
    onEmailChange: (String) -> Unit,
    onContinue: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Progress indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFF9B8FC7), RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                )
            }

            Text(
                text = "Email",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = { Text("sarah.jansen@gmail.com", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9B8FC7),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9B8FC7),
                    disabledContainerColor = Color(0xFF9B8FC7).copy(alpha = 0.5f)
                ),
                enabled = email.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Create an account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Text(
                text = "By using Budget Buddy, you agree to the\nTerms and Privacy Policy.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun UsernameStep(
    username: String,
    onUsernameChange: (String) -> Unit,
    onContinue: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Progress indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFF9B8FC7), RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFF9B8FC7), RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                )
            }

            Text(
                text = "Username",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = { Text("Enter your username", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9B8FC7),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9B8FC7),
                    disabledContainerColor = Color(0xFF9B8FC7).copy(alpha = 0.5f)
                ),
                enabled = username.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Text(
                text = "By using Budget Buddy, you agree to the\nTerms and Privacy Policy.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun PasswordStep(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    hasMinLength: Boolean,
    hasNumber: Boolean,
    hasSymbol: Boolean,
    passwordStrength: Float,
    strengthColor: Color,
    onContinue: () -> Unit,
    isLoading: Boolean,
    continueEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Progress indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFF9B8FC7), RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFF9B8FC7), RoundedCornerShape(2.dp))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(Color(0xFF9B8FC7), RoundedCornerShape(2.dp))
                )
            }

            Text(
                text = "Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Enter password", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9B8FC7),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                singleLine = true
            )

            // Password strength indicator
            if (password.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(4.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(passwordStrength)
                            .height(4.dp)
                            .background(strengthColor, RoundedCornerShape(2.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Validation checklist
            ValidationItem(
                text = "8 characters minimum",
                isValid = hasMinLength
            )
            Spacer(modifier = Modifier.height(12.dp))
            ValidationItem(
                text = "a number",
                isValid = hasNumber
            )
            Spacer(modifier = Modifier.height(12.dp))
            ValidationItem(
                text = "a symbol",
                isValid = hasSymbol
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9B8FC7),
                    disabledContainerColor = Color(0xFF9B8FC7).copy(alpha = 0.5f)
                ),
                enabled = continueEnabled && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Text(
                text = "By using Budget Buddy, you agree to the\nTerms and Privacy Policy.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun ValidationItem(text: String, isValid: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = if (isValid) Color(0xFF4CAF50) else Color.Transparent,
                    shape = CircleShape
                )
                .then(
                    if (!isValid) Modifier.background(
                        Color.Transparent,
                        CircleShape
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isValid) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Valid",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFFE0E0E0), CircleShape)
                )
            }
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isValid) Color(0xFF4CAF50) else Color.Gray,
            fontWeight = if (isValid) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun SuccessStep(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success icon (flower-like shape from mockup)
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFFE8D5F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color(0xFF9B8FC7),
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Your account\nwas successfully created!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Only one click to explore Budget Buddy.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9B8FC7)
            )
        ) {
            Text(
                text = "Log in",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "By using Budget Buddy, you agree to the\nTerms and Privacy Policy.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

