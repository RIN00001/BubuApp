package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.todolistapp.R
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.StringDataStatusUIState
import com.example.todolistapp.uiStates.TodoDetailDataStatusUIState
import com.example.todolistapp.viewModels.TodoDetailViewModel
import com.example.todolistapp.viewModels.TodoListFormViewModel

@Composable
fun TodoListDetailView (
    modifier: Modifier = Modifier,
    navController: NavHostController,
    todoDetailViewModel: TodoDetailViewModel,
    token: String,
    context: Context,
    todoListFormViewModel: TodoListFormViewModel
) {
    val getTodoStatus = todoDetailViewModel.getTodoStatus
    val deleteTodoStatus = todoDetailViewModel.deleteTodoStatus

    LaunchedEffect(deleteTodoStatus) {
        if (deleteTodoStatus is StringDataStatusUIState.Failed) {
            Toast.makeText(context, deleteTodoStatus.errorMessage, Toast.LENGTH_SHORT).show()
            todoDetailViewModel.clearDeleteTodoErrorMessage()
        }
    }

    when(getTodoStatus) {
        is TodoDetailDataStatusUIState.Success -> Column(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .size(40.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }

                Row {
                    Button(
                        onClick = {
                            todoListFormViewModel.navigateToUpdateForm(navController, getTodoStatus.data)
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .size(40.dp),
                        colors = ButtonDefaults.buttonColors(Color.Blue)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_update),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier
                                .padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                    Button(
                        onClick = {
                            todoDetailViewModel.deleteTodo(token, getTodoStatus.data.id, navController)
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .size(40.dp),
                        colors = ButtonDefaults.buttonColors(Color.Red)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier
                                .padding(10.dp)
                        )
                    }
                }
            }

            // add image here if you need

            Text(
                text = getTodoStatus.data.title,
                fontSize = 35.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 16.dp)
            )

            Row(
                modifier = Modifier
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.status_detail_text),
                    fontSize = 17.sp
                )
                Text(
                    text = getTodoStatus.data.status,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .padding(4.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.priority_detail_text),
                    fontSize = 17.sp
                )
                Text(
                    text = getTodoStatus.data.priority,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .padding(4.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.due_date_detail_text),
                    fontSize = 17.sp
                )
                Text(
                    text = getTodoStatus.data.dueDate,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .padding(4.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.description_detail_text),
                    fontSize = 17.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = getTodoStatus.data.description,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .padding(8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        else -> Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Data Found!",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }


}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TodoListDetailViewPreview() {
    TodoListDetailView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        navController = rememberNavController(),
        todoDetailViewModel = viewModel(factory = TodoDetailViewModel.Factory),
        token = "",
        context = LocalContext.current,
        todoListFormViewModel = viewModel(factory = TodoListFormViewModel.Factory),
    )
}