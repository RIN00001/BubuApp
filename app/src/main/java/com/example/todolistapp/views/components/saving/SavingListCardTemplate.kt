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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.todolistapp.uiStates.SavingDataStatusUIState

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
            containerColor = Color(0xFFE0F7FA)
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
                        color = Color(0xFF00796B)
                    )
                    Text(
                        text = "Amount: $${savingModel.amount}",
                        fontSize = 16.sp,
                        color = Color(0xFF004D40)
                    )
                    Text(
                        text = "Target: $${savingModel.targetamount}",
                        fontSize = 16.sp,
                        color = Color(0xFF004D40)
                    )

                    val progress = if (savingModel.targetamount > 0) {
                        (savingModel.amount / savingModel.targetamount * 100).toInt()
                    } else 0
                    Text(
                        text = "Progress: $progress%",
                        fontSize = 14.sp,
                        color = Color(0xFF00796B),
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onEditClick(savingModel) },
                            colors = ButtonDefaults.buttonColors(Color(0xFF2196F3)),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Button(
                            onClick = { onDeleteClick(savingModel) },
                            colors = ButtonDefaults.buttonColors(Color(0xFFF44336)),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "Delete",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { onAddAmountClick(savingModel) },
                colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "+ Add Amount",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
