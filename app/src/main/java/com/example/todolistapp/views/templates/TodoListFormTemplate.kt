package com.example.todolistapp.views.templates

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todolistapp.R

@Composable
fun TodoListOutlinedTextField(
    inputValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String,
    placeholderText: String,
    minLine: Int,
    maxLine: Int
) {
    OutlinedTextField(
        value = inputValue,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(text = labelText )
        },
        placeholder = {
            Text(text = placeholderText)
        },
        modifier = modifier,
        minLines = minLine,
        maxLines = maxLine
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListDropdown(
    dropdownMenuExpanded: Boolean,
    onDropdownMenuBoxExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dropdownItems: List<String>,
    onDropdownItemClick: (String) -> Unit,
    selectedItem: String,
    labelText: String
) {
    ExposedDropdownMenuBox(
        expanded = dropdownMenuExpanded,
        onExpandedChange = onDropdownMenuBoxExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(),
            value = selectedItem,
            label = {
                Text(text = labelText)
            },
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownMenuExpanded)
            },
        )
        
        ExposedDropdownMenu(
            expanded = dropdownMenuExpanded,
            onDismissRequest = onDismissRequest
        ) {
            dropdownItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item)
                    },
                    onClick = {
                        onDropdownItemClick(item)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListDatePicker(
    datePickerValue: String,
    showCalendarDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Make the date picker here
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = datePickerValue,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(text = stringResource(R.string.pick_date_text))
            },
            modifier = Modifier
                .weight(4f)
        )

        Button(
            onClick = showCalendarDialog,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(50.dp)
                .weight(0.75f),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }
}