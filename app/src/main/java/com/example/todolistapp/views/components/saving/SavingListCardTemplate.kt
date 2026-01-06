package com.example.todolistapp.views.templates

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.R
import com.example.todolistapp.models.SavingModel
import com.example.todolistapp.utils.formatRupiah

@Composable
fun SavingListCardTemplate(
    modifier: Modifier = Modifier,
    savingModel: SavingModel,
    onEditClick: (SavingModel) -> Unit = {},
    onDeleteClick: (SavingModel) -> Unit = {},
    onAddAmountClick: (SavingModel) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F1FA)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = savingModel.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A4C93)
                    )
                    Text(
                        text = "Target: ${formatRupiah(savingModel.targetamount)}",
                        fontSize = 14.sp,
                        color = Color(0xFF8B7BA8)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Button(
                            onClick = { onAddAmountClick(savingModel) },
                            colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                            contentPadding = PaddingValues(10.dp),
                            modifier = Modifier.size(50.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = "Add Amount",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onEditClick(savingModel) },
                            colors = ButtonDefaults.buttonColors(Color(0xFF2196F3)),
                            contentPadding = PaddingValues(10.dp),
                            modifier = Modifier.size(50.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onDeleteClick(savingModel) },
                            colors = ButtonDefaults.buttonColors(Color(0xFFF44336)),
                            contentPadding = PaddingValues(10.dp),
                            modifier = Modifier.size(50.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Progress Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Terkumpul",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Target",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatRupiah(savingModel.amount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9B8FC7)
                    )

                    val progress = if (savingModel.targetamount > 0) {
                        (savingModel.amount / savingModel.targetamount * 100).toInt()
                    } else 0
                    Text(
                        text = "$progress%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9B8FC7)
                    )
                }

                // Progress Bar
                if (savingModel.targetamount > 0) {
                    val progressValue = (savingModel.amount / savingModel.targetamount).coerceIn(0.0, 1.0).toFloat()
                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(top = 12.dp)
                            .background(
                                color = Color(0xFFE8DDF5),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        color = Color(0xFF9B8FC7),
                        trackColor = Color(0xFFE8DDF5)
                    )
                }
            }

            // Display Goal Date if exists
            if (savingModel.goalDate.isNotEmpty() && savingModel.goalDate.trim().isNotEmpty()) {
                // Use regex to extract only YYYY-MM-DD format
                val dateRegex = Regex("(\\d{4}-\\d{2}-\\d{2})")
                val matchResult = dateRegex.find(savingModel.goalDate)
                val cleanDate = matchResult?.groupValues?.get(1) ?: ""

                // Only show if it's a valid date and not default date
                if (cleanDate.isNotEmpty() && cleanDate != "1970-01-01") {
                    Text(
                        text = "Tanggal Target: $cleanDate",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}
