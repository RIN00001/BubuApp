package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.todolistapp.R
import com.example.todolistapp.uiStates.StringDataStatusUIState
import com.example.todolistapp.viewModels.SavingDetailViewModel
import com.example.todolistapp.viewModels.SavingListFormViewModel
import com.example.todolistapp.views.templates.CircleLoadingTemplate

@Composable
fun SavingListFormView(
    savingListFormViewModel: SavingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory),
    modifier: Modifier = Modifier,
    context: Context,
    navController: NavHostController,
    savingDetailViewModel: SavingDetailViewModel,
    token: String,
    userId: Int
) {
    val savingListFormUIState = savingListFormViewModel.savingListFormUIState.collectAsState()
    val submissionStatus = savingListFormViewModel.submissionStatus

    LaunchedEffect(submissionStatus) {
        if (submissionStatus is StringDataStatusUIState.Failed) {
            Toast.makeText(context, submissionStatus.errorMessage, Toast.LENGTH_SHORT).show()
            savingListFormViewModel.clearErrorMessage()
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

        // Title dengan background box ungu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 24.dp)
                .background(
                    color = Color(0xFFAD88C6),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (savingListFormViewModel.isUpdate) "Edit Tabungan" else "Buat Tabungan Baru",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        // Form Content - Scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Saving Name
            SavingTextField(
                inputValue = savingListFormViewModel.nameInput,
                onValueChange = {
                    savingListFormViewModel.changeNameInput(it)
                    savingListFormViewModel.checkNullFormValues()
                },
                modifier = Modifier.fillMaxWidth(),
                labelText = "Nama Tabungan",
                placeholderText = "Contoh: Tabungan Liburan",
                minLine = 1,
                maxLine = 1
            )

            // Target Amount
            SavingTextField(
                inputValue = savingListFormViewModel.targetAmountInput,
                onValueChange = {
                    savingListFormViewModel.changeTargetAmountInput(it)
                    savingListFormViewModel.checkNullFormValues()
                },
                labelText = "Target Jumlah",
                placeholderText = "Contoh: 5000000",
                minLine = 1,
                maxLine = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            // Current Amount
            SavingTextField(
                inputValue = savingListFormViewModel.currentAmountInput,
                onValueChange = {
                    savingListFormViewModel.changeCurrentAmountInput(it)
                    savingListFormViewModel.checkNullFormValues()
                },
                labelText = "Jumlah Saat Ini",
                placeholderText = "Contoh: 1000000",
                minLine = 1,
                maxLine = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            // Due Date (Optional)
            Text(
                text = "Tanggal Target (Opsional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424242),
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
            )

            DatePicker(
                datePickerValue = savingListFormViewModel.dueDateInput,
                showCalendarDialog = {
                    savingListFormViewModel.showDatePickerDialog(savingListFormViewModel.initDatePickerDialog(context))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Kosongkan atau hapus jika tidak ingin menetapkan tanggal target",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 24.dp)
            )
        }

        // Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    color = Color.Black
                )
            }

            when(submissionStatus) {
                is StringDataStatusUIState.Loading -> CircleLoadingTemplate(
                    color = Color(0xFFAD88C6),
                    trackColor = Color.Transparent,
                    modifier = Modifier
                        .padding(0.dp)
                )
                else -> Button(
                    onClick = {
                        if (token.isEmpty()) {
                            Toast.makeText(context, "Token tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (savingListFormViewModel.isUpdate) {
                            savingListFormViewModel.updateSaving(
                                token = token,
                                userId = userId,
                                navController = navController
                            )
                        } else {
                            savingListFormViewModel.createSaving(navController, token, userId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    enabled = savingListFormUIState.value.saveButtonEnabled && token.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (savingListFormUIState.value.saveButtonEnabled && token.isNotEmpty()) Color(0xFFAD88C6) else Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.save_text),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (savingListFormUIState.value.saveButtonEnabled && token.isNotEmpty()) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun SavingListFormViewPreview() {
    SavingListFormView(
        modifier = Modifier.fillMaxSize(),
        context = LocalContext.current,
        navController = rememberNavController(),
        savingDetailViewModel = viewModel(factory = SavingDetailViewModel.Factory),
        token = "test_token",
        userId = 1
    )
}
