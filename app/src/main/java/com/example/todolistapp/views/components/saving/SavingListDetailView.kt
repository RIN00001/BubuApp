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
import androidx.compose.ui.draw.shadow
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
import com.example.todolistapp.utils.formatRupiah
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
            .background(Color(0xFFFAFAFA))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Text(
                text = "Detail Tabungan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Button(
                onClick = {
                    savingDetailViewModel.deleteSaving(token, userId, savingId, navController)
                },
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(Color(0xFFE53935)),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(22.dp)
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
                    val progressPercentage = if (saving.targetamount > 0) {
                        (saving.amount / saving.targetamount * 100).toInt().coerceIn(0, 100)
                    } else {
                        0
                    }

                    val isCompleted = progressPercentage >= 100

                    val cardColor = when {
                        isCompleted -> Color(0xFFE8F5E9)
                        progressPercentage >= 75 -> Color(0xFFFFF3E0)
                        progressPercentage >= 50 -> Color(0xFFE3F2FD)
                        else -> Color(0xFFF3E5F5)
                    }

                    val progressColor = when {
                        isCompleted -> Color(0xFF4CAF50)
                        progressPercentage >= 75 -> Color(0xFFFF9800)
                        progressPercentage >= 50 -> Color(0xFF2196F3)
                        else -> Color(0xFF9C27B0)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(cardColor),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = saving.name,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            if (saving.goalDate.isNotEmpty()) {
                                Text(
                                    text = "Tanggal Target: ${saving.goalDate}",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 20.dp)
                                )
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color.Black.copy(alpha = 0.1f)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Target",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = formatRupiah(saving.targetamount),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "Terkumpul",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = formatRupiah(saving.amount),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = progressColor
                                    )
                                }
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color.Black.copy(alpha = 0.1f)
                            )

                            val progress = (saving.amount / saving.targetamount).coerceIn(0.0, 1.0).toFloat()

                            Text(
                                text = "Progress: ${String.format(Locale.getDefault(), "%.1f", progress * 100)}%",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = progressColor,
                                trackColor = Color.White.copy(alpha = 0.5f)
                            )

                            if (isCompleted) {
                                Surface(
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .align(Alignment.CenterHorizontally)
                                        .clip(RoundedCornerShape(10.dp)),
                                    color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "✓ Target Tercapai!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            savingListFormViewModel.navigateToUpdateForm(navController, saving)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(Color(0xFF2196F3)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Edit Tabungan",
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
                        text = "Gagal memuat detail tabungan!",
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
