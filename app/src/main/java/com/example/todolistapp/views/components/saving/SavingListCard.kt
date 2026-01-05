package com.example.todolistapp.views.components.saving

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.viewModels.SharedDataViewModel
import com.example.todolistapp.viewModels.SavingListFormViewModel

@Composable
fun SavingListCard(
    saving: SavingModel,
    navController: NavHostController,
    savingListFormViewModel: SavingListFormViewModel,
    onDelete: (Int) -> Unit
) {
    val progressPercentage = if (saving.targetamount > 0) {
        (saving.amount / saving.targetamount * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    val isCompleted = progressPercentage >= 100

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header dengan Nama dan Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = saving.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Target: \$${String.format("%.2f", saving.targetamount)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Button Add Amount
                    IconButton(
                        onClick = {
                            SharedDataViewModel.currentSavingModel = saving
                            navController.navigate(PagesEnum.AddAmount.name)
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Amount",
                            tint = Color.Green
                        )
                    }

                    // Button Edit
                    IconButton(
                        onClick = {
                            savingListFormViewModel.navigateToUpdateForm(navController, saving)
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Blue
                        )
                    }

                    // Button Delete
                    IconButton(
                        onClick = { onDelete(saving.id) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray
            )

            // Progress Section
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                // Current Amount dan Percentage
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Progress",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "\$${String.format("%.2f", saving.amount)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Of \$${String.format("%.2f", saving.targetamount)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$progressPercentage%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) Color.Green else Color.Blue
                        )
                    }
                }

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progressPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    trackColor = Color.LightGray,
                    color = if (isCompleted) Color.Green else Color.Blue
                )

                // Goal Date
                Text(
                    text = "Goal Date: ${saving.goalDate}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 10.dp)
                )

                // Completion Badge
                if (isCompleted) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = "✓ Target Completed!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}
