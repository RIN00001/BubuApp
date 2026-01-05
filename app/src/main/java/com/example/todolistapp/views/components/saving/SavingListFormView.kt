//package com.example.todolistapp.views
//
//import android.content.Context
//import android.widget.Toast
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.rememberNavController
//import com.example.todolistapp.R
//import com.example.todolistapp.uiStates.StringDataStatusUIState
//import com.example.todolistapp.viewModels.SavingDetailViewModel
//import com.example.todolistapp.viewModels.SavingListFormViewModel
//import com.example.todolistapp.views.templates.CircleLoadingTemplate
//
//
//@Composable
//fun SavingListFormView(
//    savingListFormViewModel: SavingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory),
//    modifier: Modifier = Modifier,
//    context: Context,
//    navController: NavHostController,
//    savingDetailViewModel: SavingDetailViewModel,
//    token: String,
//    userId: Int
//) {
//    val savingListFormUIState = savingListFormViewModel.savingListFormUIState.collectAsState()
//    val submissionStatus = savingListFormViewModel.submissionStatus
//
//    LaunchedEffect(submissionStatus) {
//        if (submissionStatus is StringDataStatusUIState.Failed) {
//            Toast.makeText(context, submissionStatus.errorMessage, Toast.LENGTH_SHORT).show()
//            savingListFormViewModel.clearErrorMessage()
//        }
//    }
//
//    Column(
//        modifier = modifier,
//        verticalArrangement = Arrangement.SpaceBetween
//    ) {
//
//        Column {
//            Text(
//                text = if (savingListFormViewModel.isUpdate) "Edit Saving" else "Create Saving",
//                fontSize = 35.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier
//                    .padding(bottom = 15.dp)
//            )
//
//            TodoListOutlinedTextField(
//                inputValue = savingListFormViewModel.nameInput,
//                onValueChange = {
//                    savingListFormViewModel.changeNameInput(it)
//                    savingListFormViewModel.checkNullFormValues()
//                },
//                modifier = Modifier
//                    .fillMaxWidth(),
//                labelText = "Saving Name",
//                placeholderText = "Enter saving name",
//                minLine = 1,
//                maxLine = 1
//            )
//
//            TodoListOutlinedTextField(
//                inputValue = savingListFormViewModel.targetAmountInput,
//                onValueChange = {
//                    savingListFormViewModel.changeTargetAmountInput(it)
//                    savingListFormViewModel.checkNullFormValues()
//                },
//                labelText = "Target Amount",
//                placeholderText = "Enter target amount",
//                minLine = 1,
//                maxLine = 1,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 4.dp)
//            )
//
//            TodoListOutlinedTextField(
//                inputValue = savingListFormViewModel.currentAmountInput,
//                onValueChange = {
//                    savingListFormViewModel.changeCurrentAmountInput(it)
//                    savingListFormViewModel.checkNullFormValues()
//                },
//                labelText = "Current Amount",
//                placeholderText = "Enter current amount",
//                minLine = 1,
//                maxLine = 1,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 4.dp)
//            )
//
//            TodoListDatePicker(
//                datePickerValue = savingListFormViewModel.dueDateInput,
//                showCalendarDialog = {
//                    savingListFormViewModel.showDatePickerDialog(savingListFormViewModel.initDatePickerDialog(context))
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 12.dp)
//            )
//        }
//
//        Column {
//            Button(
//                onClick = {
//                    navController.popBackStack()
//                },
//                modifier = Modifier
//                    .fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(Color.Gray)
//            ) {
//                Text(text = stringResource(R.string.cancel_text))
//            }
//
//            when(submissionStatus) {
//                is StringDataStatusUIState.Loading -> CircleLoadingTemplate(
//                    color = Color.Blue,
//                    trackColor = Color.Transparent,
//                    modifier = Modifier
//                        .padding(top = 4.dp)
//                )
//                else -> Button(
//                    onClick = {
//                        if (savingListFormViewModel.isUpdate) {
//                            savingListFormViewModel.updateSaving(
//                                token = token,
//                                userId = userId,
//                                navController = navController
//                            )
//                        } else {
//                            savingListFormViewModel.createSaving(navController, token, userId)
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    enabled = savingListFormUIState.value.saveButtonEnabled,
//                    colors = ButtonDefaults.buttonColors(savingListFormViewModel.changeSaveButtonColor())
//                ) {
//                    Text(text = stringResource(R.string.save_text))
//                }
//            }
//        }
//    }
//}
//
//@Preview(
//    showSystemUi = true,
//    showBackground = true
//)
//@Composable
//fun SavingListFormViewPreview() {
//    SavingListFormView(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(8.dp)
//            .padding(top = 8.dp),
//        context = LocalContext.current,
//        navController = rememberNavController(),
//        savingDetailViewModel = viewModel(factory = SavingDetailViewModel.Factory),
//        token = "test_token",
//        userId = 1
//    )
//}
