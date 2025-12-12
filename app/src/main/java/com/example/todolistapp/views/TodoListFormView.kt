package com.example.todolistapp.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.todolistapp.viewModels.TodoDetailViewModel
import com.example.todolistapp.viewModels.TodoListFormViewModel
import com.example.todolistapp.views.templates.CircleLoadingTemplate
import com.example.todolistapp.views.templates.TodoListDatePicker
import com.example.todolistapp.views.templates.TodoListDropdown
import com.example.todolistapp.views.templates.TodoListOutlinedTextField

@Composable
fun TodoListFormView(
    todoListFormViewModel: TodoListFormViewModel = viewModel(),
    modifier: Modifier = Modifier,
    context: Context,
    navController: NavHostController,
    todoDetailViewModel: TodoDetailViewModel,
    token: String
) {
    val todoListFormUIState = todoListFormViewModel.todoListFormUIState.collectAsState()
    val submissionStatus = todoListFormViewModel.submissionStatus

    LaunchedEffect(submissionStatus) {
        if (submissionStatus is StringDataStatusUIState.Failed) {
            Toast.makeText(context, submissionStatus.errorMessage, Toast.LENGTH_SHORT).show()
            todoListFormViewModel.clearErrorMessage()
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(
                text = "Todo List Form",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 15.dp)
            )

            TodoListOutlinedTextField(
                inputValue = todoListFormViewModel.titleInput,
                onValueChange = {
                    todoListFormViewModel.changeTitleInput(it)
                    todoListFormViewModel.checkNullFormValues()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                labelText = stringResource(R.string.title_text),
                placeholderText = stringResource(R.string.title_text),
                minLine = 1,
                maxLine = 1
            )

            TodoListOutlinedTextField(
                inputValue = todoListFormViewModel.descriptionInput,
                onValueChange = {
                    todoListFormViewModel.changeDescriptionInput(it)
                    todoListFormViewModel.checkNullFormValues()
                },
                labelText = stringResource(R.string.description_text),
                placeholderText = stringResource(R.string.description_text),
                minLine = 10,
                maxLine = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
            ) {
                TodoListDropdown(
                    dropdownMenuExpanded = todoListFormUIState.value.statusDropdownExpandedValue,
                    onDismissRequest = {
                        todoListFormViewModel.dismissStatusDropdownMenu()
                    },
                    dropdownItems = todoListFormUIState.value.statusDropdownItems,
                    onDropdownItemClick = {
                        todoListFormViewModel.changeStatusInput(it)
                        todoListFormViewModel.checkNullFormValues()
                    },
                    onDropdownMenuBoxExpandedChange = {
                        todoListFormViewModel.changeStatusExpandedValue(it)
                    },
                    selectedItem = todoListFormViewModel.statusInput,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .weight(1f),
                    labelText = "Status"
                )

                TodoListDropdown(
                    dropdownMenuExpanded = todoListFormUIState.value.priorityDropdownExpandedValue,
                    onDismissRequest = {
                        todoListFormViewModel.dismissPriorityDropdownMenu()
                    },
                    dropdownItems = todoListFormUIState.value.priorityDropdownItems,
                    onDropdownItemClick = {
                        todoListFormViewModel.changePriorityInput(it)
                        todoListFormViewModel.checkNullFormValues()
                    },
                    onDropdownMenuBoxExpandedChange = {
                        todoListFormViewModel.changePriorityExpandedValue(it)
                    },
                    selectedItem = todoListFormViewModel.priorityInput,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .weight(1f),
                    labelText = "Priority"
                )
            }

            // TODO: Date Picker
            TodoListDatePicker(
                datePickerValue = todoListFormViewModel.dueDateInput,
                showCalendarDialog = {
                    todoListFormViewModel.showDatePickerDialog(todoListFormViewModel.initDatePickerDialog(context))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }

        Column {
            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Gray)
            ) {
                Text(text = stringResource(R.string.cancel_text))
            }

            when(submissionStatus) {
                is StringDataStatusUIState.Loading -> CircleLoadingTemplate(
                    color = Color.Blue,
                    trackColor = Color.Transparent,
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
                else -> Button(
                    onClick = {
                        if (todoListFormViewModel.isUpdate) {
                            todoListFormViewModel.updateTodo(token, getTodo = {
                                todoDetailViewModel.getTodo(token, todoListFormViewModel.todoId, navController, todoListFormViewModel.isUpdate)
                            }, navController)
                        } else {
                            todoListFormViewModel.createTodo(navController, token)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = todoListFormUIState.value.saveButtonEnabled,
                    colors = ButtonDefaults.buttonColors(todoListFormViewModel.changeSaveButtonColor())
                ) {
                    Text(text = stringResource(R.string.save_text))
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
fun CreateTodoListViewPreview() {
    TodoListFormView(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .padding(top = 8.dp),
        context = LocalContext.current,
        navController = rememberNavController(),
        todoDetailViewModel = viewModel(factory = TodoDetailViewModel.Factory),
        token = ""
    )
}