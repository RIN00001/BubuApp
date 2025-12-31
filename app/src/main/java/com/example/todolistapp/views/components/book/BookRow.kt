package com.example.todolistapp.views.components.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolistapp.uiStates.BookListStatusUIState

@Composable
fun BookRow(
    listState: BookListStatusUIState,
    selectedBookId: Int?,
    onBookSelected: (Int) -> Unit,
    onAddBookClick: () -> Unit
) {
    when (listState) {
        is BookListStatusUIState.Success -> {
            val books = listState.data
            val displayBooks = books.take(3)
            val currentSelectedId = selectedBookId ?: displayBooks.firstOrNull()?.id

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayBooks) { book ->
                    BookIcon(
                        book = book,
                        isSelected = book.id == currentSelectedId,
                        onClick = { onBookSelected(book.id) }
                    )
                }

                // Add Book button - always in 4th slot
                item {
                    AddBookButton(onClick = onAddBookClick)
                }
            }
        }
        is BookListStatusUIState.Loading -> {
            CircularProgressIndicator(color = Color.White)
        }
        else -> {
            // Show Add button only
            AddBookButton(onClick = onAddBookClick)
        }
    }
}
