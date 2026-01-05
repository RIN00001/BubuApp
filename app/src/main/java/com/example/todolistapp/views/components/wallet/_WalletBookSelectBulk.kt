package com.example.todolistapp.views.components.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.models.BookModel

@Composable
fun WalletBookAttachmentSection(
    currentBookId: Int?,
    currentBookName: String?,
    allBooks: List<BookModel>,
    attachedBookIds: Set<Int>,
    onToggleBook: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title
        Text(
            text = "Book Attachments",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1B1F)
        )

        // Use in Current Book
        if (currentBookId != null && currentBookName != null) {
            CurrentBookToggle(
                bookId = currentBookId,
                bookName = currentBookName,
                isAttached = attachedBookIds.contains(currentBookId),
                onToggle = { isChecked ->
                    onToggleBook(currentBookId, isChecked)
                }
            )
        }

        // Bulk Book Attach (Expandable)
        BulkBookAttach(
            allBooks = allBooks.filter { it.id != currentBookId },
            attachedBookIds = attachedBookIds,
            onToggleBook = onToggleBook
        )
    }
}

@Composable
fun CurrentBookToggle(
    @Suppress("UNUSED_PARAMETER") bookId: Int,
    bookName: String,
    isAttached: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Use in Current Book",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1B1F)
                )
                Text(
                    text = bookName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Switch(
                checked = isAttached,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF7469B6),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}

@Composable
fun BulkBookAttach(
    allBooks: List<BookModel>,
    attachedBookIds: Set<Int>,
    onToggleBook: (Int, Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header (Clickable to expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bulk Book Attach",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1B1F)
                    )
                    Text(
                        text = "${attachedBookIds.size} books attached",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Color(0xFF7469B6)
                )
            }

            // Expandable Book List
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

                    if (allBooks.isEmpty()) {
                        Text(
                            text = "No other books available",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        allBooks.forEach { book ->
                            BookToggleItem(
                                book = book,
                                isAttached = attachedBookIds.contains(book.id),
                                onToggle = { isChecked ->
                                    onToggleBook(book.id, isChecked)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookToggleItem(
    book: BookModel,
    isAttached: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = book.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1C1B1F)
            )
            if (book.program != null) {
                Text(
                    text = book.program,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        Switch(
            checked = isAttached,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF7469B6),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}
