package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolistapp.R
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.uiStates.StringDataStatusUIState
import com.example.todolistapp.utils.formatRupiah
import com.example.todolistapp.viewModels.HomeViewModel
import com.example.todolistapp.viewModels.SavingListFormViewModel
import com.example.todolistapp.viewModels.SharedDataViewModel
import com.example.todolistapp.views.templates.CircleLoadingTemplate

@Composable
fun SavingAddAmountView(
    modifier: Modifier = Modifier,
    context: Context,
    navController: NavHostController,
    savingListFormViewModel: SavingListFormViewModel,
    homeViewModel: HomeViewModel,
    token: String,
    userId: Int,
    savingModel: SavingModel
) {
    var amountToAdd by remember { mutableStateOf("") }
    var isFormValid by remember { mutableStateOf(false) }
    val submissionStatus = savingListFormViewModel.submissionStatus

    LaunchedEffect(amountToAdd) {
        val amount = amountToAdd.toDoubleOrNull()
        isFormValid = amount != null && amount > 0
    }

    LaunchedEffect(submissionStatus) {
        when (submissionStatus) {
            is StringDataStatusUIState.Success -> {
                Toast.makeText(context, submissionStatus.data, Toast.LENGTH_SHORT).show()
                savingListFormViewModel.clearErrorMessage()
                SharedDataViewModel.currentSavingModel = null
                navController.navigate(PagesEnum.Saving.name) {
                    popUpTo(PagesEnum.AddAmount.name) {
                        inclusive = true
                    }
                }
            }
            is StringDataStatusUIState.Failed -> {
                Toast.makeText(context, submissionStatus.errorMessage, Toast.LENGTH_SHORT).show()
                savingListFormViewModel.clearErrorMessage()
            }
            else -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(16.dp)
    ) {
        // Header dengan Back Button
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(Color.White),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier.size(22.dp)
            )
        }

        // Title
        Text(
            text = "Tambah Jumlah",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
        )

        // Content - Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Saving Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color(0xFFF3E5F5)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = savingModel.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color.Black.copy(alpha = 0.1f)
                    )

                    // Current Amount
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "Jumlah Saat Ini",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = formatRupiah(savingModel.amount),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9C27B0)
                        )
                    }

                    // Target Amount
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "Target Jumlah",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = formatRupiah(savingModel.targetamount),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color.Black.copy(alpha = 0.1f)
                    )

                    // Progress
                    val progressPercentage = if (savingModel.targetamount > 0) {
                        (savingModel.amount / savingModel.targetamount * 100).toInt().coerceIn(0, 100)
                    } else {
                        0
                    }

                    Text(
                        text = "Progress: $progressPercentage%",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Amount Input Section
            Text(
                text = "Jumlah yang Ditambahkan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            SavingTextField(
                inputValue = amountToAdd,
                onValueChange = { amountToAdd = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                labelText = "Jumlah",
                placeholderText = "Contoh: 500000",
                minLine = 1,
                maxLine = 1
            )
        }

        // Buttons Section
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(Color(0xFFE0E0E0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.cancel_text),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )
            }

            when (submissionStatus) {
                is StringDataStatusUIState.Loading -> CircleLoadingTemplate(
                    color = Color(0xFFAD88C6),
                    trackColor = Color.Transparent,
                    modifier = Modifier.padding(top = 12.dp)
                )
                else -> Button(
                    onClick = {
                        val amount = amountToAdd.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            savingListFormViewModel.addAmountToSaving(
                                token = token,
                                userId = savingModel.userId,
                                savingModel = savingModel,
                                amountToAdd = amount,
                                navController = navController,
                                onSuccess = {
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 12.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) Color(0xFFAD88C6) else Color(0xFFE0E0E0),
                        disabledContainerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Tambah Jumlah",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFormValid) Color.White else Color(0xFF999999)
                    )
                }
            }
        }
    }
}
