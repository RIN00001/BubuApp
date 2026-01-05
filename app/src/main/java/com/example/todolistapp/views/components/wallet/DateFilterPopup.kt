package com.example.todolistapp.views.components.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*

enum class DateFilterOption {
    THIS_WEEK,
    LAST_WEEK,
    THIS_MONTH,
    LAST_MONTH,
    CUSTOM
}

data class DateRange(
    val startDate: String,
    val endDate: String,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFilterPopup(
    currentFilter: DateRange,
    onDismiss: () -> Unit,
    onFilterSelected: (DateRange) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Date Range",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                HorizontalDivider()

                // Quick options
                QuickFilterOption(
                    label = "This Week",
                    isSelected = currentFilter.label == "This Week",
                    onClick = {
                        val range = getDateRangeForOption(DateFilterOption.THIS_WEEK)
                        onFilterSelected(range)
                        onDismiss()
                    }
                )

                QuickFilterOption(
                    label = "Last Week",
                    isSelected = currentFilter.label == "Last Week",
                    onClick = {
                        val range = getDateRangeForOption(DateFilterOption.LAST_WEEK)
                        onFilterSelected(range)
                        onDismiss()
                    }
                )

                QuickFilterOption(
                    label = "This Month",
                    isSelected = currentFilter.label == "This Month",
                    onClick = {
                        val range = getDateRangeForOption(DateFilterOption.THIS_MONTH)
                        onFilterSelected(range)
                        onDismiss()
                    }
                )

                QuickFilterOption(
                    label = "Last Month",
                    isSelected = currentFilter.label == "Last Month",
                    onClick = {
                        val range = getDateRangeForOption(DateFilterOption.LAST_MONTH)
                        onFilterSelected(range)
                        onDismiss()
                    }
                )

                // Custom option - opens date pickers
                CustomDateRangeOption(
                    onFilterSelected = {
                        onFilterSelected(it)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun QuickFilterOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) Color(0xFFE8DEF8) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF7469B6) else Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangeOption(
    onFilterSelected: (DateRange) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }

    val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Custom Range",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Start Date
            OutlinedButton(
                onClick = { showStartDatePicker = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF7469B6)
                )
            ) {
                Text(
                    text = startDate?.let { displayFormat.format(it) } ?: "Start Date",
                    fontSize = 14.sp
                )
            }

            // End Date
            OutlinedButton(
                onClick = { showEndDatePicker = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF7469B6)
                )
            ) {
                Text(
                    text = endDate?.let { displayFormat.format(it) } ?: "End Date",
                    fontSize = 14.sp
                )
            }
        }

        // Apply button
        if (startDate != null && endDate != null) {
            Button(
                onClick = {
                    val range = DateRange(
                        startDate = apiFormat.format(startDate!!),
                        endDate = apiFormat.format(endDate!!),
                        label = "${displayFormat.format(startDate!!)} - ${displayFormat.format(endDate!!)}"
                    )
                    onFilterSelected(range)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7469B6)
                )
            ) {
                Text("Apply Custom Range")
            }
        }
    }

    // Date Pickers
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState()
            DatePicker(state = datePickerState)

            LaunchedEffect(datePickerState.selectedDateMillis) {
                datePickerState.selectedDateMillis?.let {
                    startDate = Date(it)
                }
            }
        }
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState()
            DatePicker(state = datePickerState)

            LaunchedEffect(datePickerState.selectedDateMillis) {
                datePickerState.selectedDateMillis?.let {
                    endDate = Date(it)
                }
            }
        }
    }
}

fun getDateRangeForOption(option: DateFilterOption): DateRange {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()

    return when (option) {
        DateFilterOption.THIS_WEEK -> {
            // Start of week (Monday)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val startOfWeek = formatter.format(calendar.time)

            // End of week (Sunday)
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            val endOfWeek = formatter.format(calendar.time)

            DateRange(
                startDate = startOfWeek,
                endDate = endOfWeek,
                label = "This Week"
            )
        }
        DateFilterOption.LAST_WEEK -> {
            // Start of last week
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            val startOfLastWeek = formatter.format(calendar.time)

            // End of last week
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            val endOfLastWeek = formatter.format(calendar.time)

            DateRange(
                startDate = startOfLastWeek,
                endDate = endOfLastWeek,
                label = "Last Week"
            )
        }
        DateFilterOption.THIS_MONTH -> {
            // Start of month
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startOfMonth = formatter.format(calendar.time)

            // End of month
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endOfMonth = formatter.format(calendar.time)

            DateRange(
                startDate = startOfMonth,
                endDate = endOfMonth,
                label = "This Month"
            )
        }
        DateFilterOption.LAST_MONTH -> {
            // Go back one month
            calendar.add(Calendar.MONTH, -1)

            // Start of last month
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startOfLastMonth = formatter.format(calendar.time)

            // End of last month
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endOfLastMonth = formatter.format(calendar.time)

            DateRange(
                startDate = startOfLastMonth,
                endDate = endOfLastMonth,
                label = "Last Month"
            )
        }
        DateFilterOption.CUSTOM -> {
            // Return current week as default
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val startOfWeek = formatter.format(calendar.time)

            calendar.add(Calendar.DAY_OF_WEEK, 6)
            val endOfWeek = formatter.format(calendar.time)

            DateRange(
                startDate = startOfWeek,
                endDate = endOfWeek,
                label = "Custom"
            )
        }
    }
}

