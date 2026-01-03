package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.todolistapp.R
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.uiStates.SavingDetailDataStatusUIState
import com.example.todolistapp.viewModels.SavingDetailViewModel
import com.example.todolistapp.viewModels.SavingListFormViewModel
import com.example.todolistapp.views.templates.CircleLoadingTemplate
import com.google.gson.Gson
import java.util.Locale

@Composable
fun SavingListDetailView(
    modifier: Modifier = Modifier,
    savingDetailViewModel: SavingDetailViewModel,
    savingListFormViewModel: SavingListFormViewModel,
    navController: NavHostController,
    token: String,
    userId: Int,
    savingId: Int,
    context: Context
) {
    val getSavingStatus = savingDetailViewModel.getSavingStatus
    val deleteSavingStatus = savingDetailViewModel.deleteSavingStatus

    LaunchedEffect(token) {
        if (token != "Unknown") {
            savingDetailViewModel.getSaving(token, userId, savingId, navController, false)
        }
    }

    LaunchedEffect(getSavingStatus) {
        if (getSavingStatus is SavingDetailDataStatusUIState.Failed) {
            Toast.makeText(context, "DATA ERROR: ${getSavingStatus.errorMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(deleteSavingStatus) {
        if (deleteSavingStatus is SavingDetailDataStatusUIState.Failed) {
            Toast.makeText(context, "DELETE ERROR: ${deleteSavingStatus.errorMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.size(45.dp),
                colors = ButtonDefaults.buttonColors(Color.LightGray),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Black),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Saving Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Button(
                onClick = {
                    savingDetailViewModel.deleteSaving(token, userId, savingId, navController)
                },
                modifier = Modifier.size(45.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),
                contentPadding = PaddingValues(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        when (getSavingStatus) {
            is SavingDetailDataStatusUIState.Success -> {
                val saving = try {
                    Gson().fromJson(getSavingStatus.data, SavingModel::class.java)
                } catch (_: Exception) {
                    null
                }
                if (saving != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = saving.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = "Goal Date: ${saving.goalDate}",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.LightGray
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Target Amount",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "$${saving.targetamount}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "Current Amount",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "$${saving.amount}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.LightGray
                            )

                            val progress = (saving.amount / saving.targetamount).coerceIn(0.0, 1.0).toFloat()

                            Text(
                                text = "Progress: ${String.format(Locale.getDefault(), "%.1f", progress * 100)}%",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color.Blue,
                                trackColor = Color.LightGray
                            )
                        }
                    }

                    Button(
                        onClick = {
                            savingListFormViewModel.navigateToUpdateForm(navController, saving)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(Color.Blue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Edit Saving",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            is SavingDetailDataStatusUIState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircleLoadingTemplate(
                        color = Color.Blue,
                        trackColor = Color.LightGray
                    )
                }
            }

            is SavingDetailDataStatusUIState.Failed -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Failed to load saving details!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }

            else -> {}
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SavingListDetailViewPreview() {
    SavingListDetailView(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        savingDetailViewModel = viewModel(),
        savingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory),
        navController = rememberNavController(),
        token = "test_token",
        userId = 1,
        savingId = 1,
        context = LocalContext.current
    )
}
