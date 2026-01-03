package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolistapp.R
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.uiStates.StringDataStatusUIState
import com.example.todolistapp.viewModels.HomeViewModel
import com.example.todolistapp.viewModels.SavingListFormViewModel
import com.example.todolistapp.viewModels.SharedDataViewModel
import com.example.todolistapp.views.templates.CircleLoadingTemplate
import com.example.todolistapp.views.templates.TodoListOutlinedTextField

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
                navController.navigate(PagesEnum.Home.name) {
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
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Add Amount",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 15.dp)
            )

            Text(
                text = "Saving: ${savingModel.name}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00796B),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Current Amount: $${savingModel.amount}",
                fontSize = 16.sp,
                color = Color(0xFF004D40),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Target Amount: $${savingModel.targetamount}",
                fontSize = 16.sp,
                color = Color(0xFF004D40),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TodoListOutlinedTextField(
                inputValue = amountToAdd,
                onValueChange = { amountToAdd = it },
                modifier = Modifier.fillMaxWidth(),
                labelText = "Amount to Add",
                placeholderText = "Enter amount to add",
                minLine = 1,
                maxLine = 1
            )
        }

        Column {
            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Gray)
            ) {
                Text(text = stringResource(R.string.cancel_text))
            }

            when (submissionStatus) {
                is StringDataStatusUIState.Loading -> CircleLoadingTemplate(
                    color = Color.Blue,
                    trackColor = Color.Transparent,
                    modifier = Modifier.padding(top = 4.dp)
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        if (isFormValid) Color.Blue else Color.LightGray
                    )
                ) {
                    Text(text = "Add Amount")
                }
            }
        }
    }
}
