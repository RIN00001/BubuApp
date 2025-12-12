package com.example.todolistapp.views.templates

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.R
import com.example.todolistapp.enums.PrioritiesEnum

@Composable
fun TodoListCardTemplate(
    title: String,
    priority: PrioritiesEnum,
    dueDate: String,
    status: String,
    modifier: Modifier = Modifier,
    priorityColor: Color,
    onCardClick: () -> Unit
) {
    Card(
        onClick = onCardClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(8f)
            ) {
                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(priorityColor)
                        )

                        Text(
                            text = priority.name[0].toString(),
                            fontSize = 13.sp,
                            modifier = Modifier
                                .padding(start = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // kasi icon jam
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp),
                        )

                        Text(
                            text = dueDate,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(start = 4.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_task),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp),
                        )

                        Text(
                            text = status,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(start = 4.dp)
                        )
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier
                    .size(15.dp)
                    .weight(0.5f)

            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TodoListCardTemplatePreview() {
    TodoListCardTemplate(
        title = "Print Hello World",
        status = "On Going",
        priority = PrioritiesEnum.High,
        dueDate = "20 September 2022",
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        priorityColor = Color.Red,
        onCardClick = {}
    )
}