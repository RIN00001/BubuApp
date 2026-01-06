package com.example.todolistapp.views.components.saving

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.utils.formatRupiah
import com.example.todolistapp.viewModels.SharedDataViewModel
import com.example.todolistapp.viewModels.SavingListFormViewModel

@Composable
fun SavingListCard(
    saving: SavingModel,
    navController: NavHostController,
    savingListFormViewModel: SavingListFormViewModel,
    onDelete: (Int) -> Unit
) {
    val showDeleteDialog = remember { mutableStateOf(false) }

    val progressPercentage = if (saving.targetamount > 0) {
        (saving.amount / saving.targetamount * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    val isCompleted = progressPercentage >= 100

    // Color scheme berdasarkan progress
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
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(cardColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header: Nama dan Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = saving.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = "Target: ${formatRupiah(saving.targetamount)}",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Action Buttons (lebih compact dengan spacing yang benar)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Button Add Amount
                    IconButton(
                        onClick = {
                            SharedDataViewModel.currentSavingModel = saving
                            navController.navigate(PagesEnum.AddAmount.name)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Amount",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Button Edit
                    IconButton(
                        onClick = {
                            savingListFormViewModel.navigateToUpdateForm(navController, saving)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFF6A4C93).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF6A4C93),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Button Delete
                    IconButton(
                        onClick = { showDeleteDialog.value = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFE53935).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            // Progress Section
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                // Current Amount dan Target
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Terkumpul",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = formatRupiah(saving.amount),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = progressColor
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Target",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$progressPercentage%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = progressColor
                        )
                    }
                }

                // Progress Bar dengan rounded corners
                LinearProgressIndicator(
                    progress = { progressPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    trackColor = Color.White.copy(alpha = 0.5f),
                    color = progressColor
                )

                // Goal Date - hanya tampilkan format YYYY-MM-DD
                if (saving.goalDate.isNotEmpty() && saving.goalDate.trim().isNotEmpty()) {
                    val dateRegex = Regex("(\\d{4}-\\d{2}-\\d{2})")
                    val matchResult = dateRegex.find(saving.goalDate)
                    val cleanDate = matchResult?.groupValues?.get(1) ?: ""

                    if (cleanDate.isNotEmpty() && cleanDate != "1970-01-01") {
                        Text(
                            text = "Tanggal Target: $cleanDate",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }

                // Completion Badge
                if (isCompleted) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .align(Alignment.Start)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "✓ Target Tercapai!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(
                    text = "Hapus Tabungan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus tabungan \"${saving.name}\"? Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog.value = false
                        onDelete(saving.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = "Hapus",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0)
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(40.dp)
                ) {
                    Text(
                        text = "Batal",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White,
            textContentColor = Color.Black
        )
    }
}

