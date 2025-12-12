package com.example.todolistapp.uiStates

data class TodoListFormUIState (
    val statusDropdownExpandedValue: Boolean = false,
    val priorityDropdownExpandedValue: Boolean = false,
    val statusDropdownItems: List<String> = listOf("To Do", "On Going", "Finished"),
    val priorityDropdownItems: List<String> = listOf("High", "Medium", "Low"),
    val showDatePickerDialog: Boolean = false,
    val saveButtonEnabled: Boolean = false
)