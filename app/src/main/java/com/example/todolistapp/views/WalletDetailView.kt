package com.example.todolistapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.R
import com.example.todolistapp.uiStates.WalletSummaryStatusUIState
import com.example.todolistapp.uiStates.BookListStatusUIState
import com.example.todolistapp.viewModels.WalletViewModel
import com.example.todolistapp.viewModels.BookViewModel
import com.example.todolistapp.views.components.wallet.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailView(
    walletId: Int,
    navController: NavHostController,
    currentBookId: Int? = null,
    walletViewModel: WalletViewModel = viewModel(factory = WalletViewModel.Factory),
    bookViewModel: BookViewModel = viewModel(factory = BookViewModel.Factory)
) {
    val summaryState by walletViewModel.summaryState.collectAsState()
    val bookListState by bookViewModel.listState.collectAsState()

    var showDateFilter by remember { mutableStateOf(false) }
    var currentDateFilter by remember { mutableStateOf(getDateRangeForOption(DateFilterOption.THIS_WEEK)) }

    // Track changes
    var editedName by remember { mutableStateOf("") }
    var attachedBookIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var hasChanges by remember { mutableStateOf(false) }
    var showSaveSuccess by remember { mutableStateOf(false) }
    var isBulkBookExpanded by remember { mutableStateOf(false) }

    // Load initial data
    LaunchedEffect(walletId) {
        walletViewModel.fetchWalletSummary(
            walletId,
            currentDateFilter.startDate,
            currentDateFilter.endDate
        )
        bookViewModel.fetchBooks()
    }

    // Initialize data from summary
    LaunchedEffect(summaryState) {
        if (summaryState is WalletSummaryStatusUIState.Success) {
            val summary = (summaryState as WalletSummaryStatusUIState.Success).data
            if (editedName.isEmpty()) {
                editedName = summary.name
            }
            if (attachedBookIds.isEmpty()) {
                attachedBookIds = summary.usedInBooks.map { it.id }.toSet()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wallet Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8A75BD) // Teal color from mockup
                )
            )
        },
        floatingActionButton = {
            if (hasChanges && summaryState is WalletSummaryStatusUIState.Success) {
                FloatingActionButton(
                    onClick = {
                        val summary = (summaryState as WalletSummaryStatusUIState.Success).data
                        val originalBookIds = summary.usedInBooks.map { it.id }.toSet()

                        // Update wallet name if changed
                        if (editedName != summary.name) {
                            walletViewModel.updateWallet(walletId, editedName, summary.balance)
                        }

                        // Handle book attachments/detachments
                        val booksToAttach = attachedBookIds - originalBookIds
                        val booksToDetach = originalBookIds - attachedBookIds

                        booksToAttach.forEach { bookId ->
                            bookViewModel.attachWallet(bookId, walletId)
                        }

                        booksToDetach.forEach { bookId ->
                            bookViewModel.detachWallet(bookId, walletId)
                        }

                        // Show success message
                        showSaveSuccess = true
                        hasChanges = false

                        // Refresh data
                        walletViewModel.fetchWalletSummary(
                            walletId,
                            currentDateFilter.startDate,
                            currentDateFilter.endDate
                        )
                    },
                    containerColor = Color(0xFFAD88C5),
                    modifier = Modifier.size(64.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.walletdetails_save),
                        contentDescription = "Save Changes",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            when (summaryState) {
                is WalletSummaryStatusUIState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }

                is WalletSummaryStatusUIState.Success -> {
                    val summary = (summaryState as WalletSummaryStatusUIState.Success).data
                    val books = when (bookListState) {
                        is BookListStatusUIState.Success -> (bookListState as BookListStatusUIState.Success).data
                        else -> emptyList()
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Wallet Summary Card
                        item {
                            WalletSummaryCard(
                                summary = summary,
                                currentDateLabel = currentDateFilter.label,
                                onDateClick = { showDateFilter = true }
                            )
                        }

                        // Editable Name Field
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.walletdetails_walletname),
                                        contentDescription = "Wallet Name",
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Wallet Name",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1C1B1F)
                                        )
                                        OutlinedTextField(
                                            value = editedName,
                                            onValueChange = {
                                                editedName = it
                                                hasChanges = true
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            placeholder = { Text("Enter wallet name") },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF00BCD4),
                                                unfocusedBorderColor = Color.LightGray,
                                                focusedTextColor = Color.Black,
                                                unfocusedTextColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        Text(
                                            text = "Currency cannot be changed",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }

                        // Book Attachments Section
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val currentBook = books.find { it.id == currentBookId }

                                // Use in Current Book
                                if (currentBook != null) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.walletdetails_currentmoney),
                                                    contentDescription = "Current Book",
                                                    modifier = Modifier.size(32.dp)
                                                )
                                                Column {
                                                    Text(
                                                        text = "Use in Current Book",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1C1B1F)
                                                    )
                                                    Text(
                                                        text = currentBook.name,
                                                        fontSize = 12.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                            Switch(
                                                checked = currentBookId?.let { attachedBookIds.contains(it) } ?: false,
                                                onCheckedChange = { isChecked ->
                                                    currentBookId?.let { bookId ->
                                                        attachedBookIds = if (isChecked) {
                                                            attachedBookIds + bookId
                                                        } else {
                                                            attachedBookIds - bookId
                                                        }
                                                        hasChanges = true
                                                    }
                                                },
                                                colors = SwitchDefaults.colors(
                                                    checkedThumbColor = Color.White,
                                                    checkedTrackColor = Color(0xFF00BCD4),
                                                    uncheckedThumbColor = Color.White,
                                                    uncheckedTrackColor = Color.LightGray
                                                )
                                            )
                                        }
                                    }
                                }

                                // Bulk Book Attach
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Header (clickable to expand/collapse)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { isBulkBookExpanded = !isBulkBookExpanded }
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.walletdetails_bulkbook),
                                                    contentDescription = "Bulk Book Attach",
                                                    modifier = Modifier.size(32.dp)
                                                )
                                                Text(
                                                    text = "Bulk Book Attach",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1C1B1F)
                                                )
                                            }
                                            Icon(
                                                imageVector = if (isBulkBookExpanded)
                                                    Icons.Default.KeyboardArrowUp
                                                else
                                                    Icons.Default.KeyboardArrowDown,
                                                contentDescription = if (isBulkBookExpanded) "Collapse" else "Expand",
                                                tint = Color.Gray
                                            )
                                        }

                                        // Expandable book list
                                        if (isBulkBookExpanded) {
                                            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                books.forEach { book ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 8.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.weight(1f)
                                                        ) {
                                                            Text(
                                                                text = book.name,
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = Color(0xFF1C1B1F)
                                                            )
                                                            Text(
                                                                text = book.program ?: "",
                                                                fontSize = 12.sp,
                                                                color = Color.Gray
                                                            )
                                                        }
                                                        Switch(
                                                            checked = attachedBookIds.contains(book.id),
                                                            onCheckedChange = { isChecked ->
                                                                attachedBookIds = if (isChecked) {
                                                                    attachedBookIds + book.id
                                                                } else {
                                                                    attachedBookIds - book.id
                                                                }
                                                                hasChanges = true
                                                            },
                                                            colors = SwitchDefaults.colors(
                                                                checkedThumbColor = Color.White,
                                                                checkedTrackColor = Color(0xFF00BCD4),
                                                                uncheckedThumbColor = Color.White,
                                                                uncheckedTrackColor = Color.LightGray
                                                            )
                                                        )
                                                    }
                                                    if (book != books.last()) {
                                                        HorizontalDivider(
                                                            modifier = Modifier.padding(vertical = 4.dp),
                                                            color = Color(0xFFF0F0F0)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Date Filter Dialog
                    if (showDateFilter) {
                        DateFilterPopup(
                            currentFilter = currentDateFilter,
                            onDismiss = { showDateFilter = false },
                            onFilterSelected = { newFilter ->
                                currentDateFilter = newFilter
                                walletViewModel.fetchWalletSummary(
                                    walletId,
                                    newFilter.startDate,
                                    newFilter.endDate
                                )
                            }
                        )
                    }

                    // Success Snackbar
                    if (showSaveSuccess) {
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(2000)
                            showSaveSuccess = false
                        }
                        Snackbar(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomCenter),
                            containerColor = Color(0xFF4CAF50)
                        ) {
                            Text("Changes saved successfully!", color = Color.White)
                        }
                    }
                }

                is WalletSummaryStatusUIState.Failed -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Failed to load wallet details",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp
                        )
                        Button(
                            onClick = {
                                walletViewModel.fetchWalletSummary(
                                    walletId,
                                    currentDateFilter.startDate,
                                    currentDateFilter.endDate
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7469B6)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

