package com.example.todolistapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bubuapp.views.components.NavigationBar
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.AuthenticationStatusUIState
import com.example.todolistapp.uiStates.SavingListUIState
import com.example.todolistapp.viewModels.AuthenticationViewModel
import com.example.todolistapp.viewModels.SavingDetailViewModel
import com.example.todolistapp.viewModels.SavingListFormViewModel
import com.example.todolistapp.viewModels.SavingListViewModel
import com.example.todolistapp.views.*
import com.example.todolistapp.views.components.book.BookDetailView
import com.example.todolistapp.views.components.book.BooksList
import com.example.todolistapp.views.components.saving.SavingListCard
import com.example.todolistapp.views.components.wallet.WalletAddEdit

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authVM: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = PagesEnum.Login.name,
        modifier = modifier
    ) {

        // ==========================================
        // 1. AUTHENTICATION FLOW
        // ==========================================
        composable(PagesEnum.Login.name) {
            LoginView(
                authenticationViewModel = authVM,
                context = context,
                navController = navController
            )

            if (authVM.authenticationStatus is AuthenticationStatusUIState.Success) {
                LaunchedEffect(Unit) {
                    navController.navigate(PagesEnum.Books.name) {
                        popUpTo(PagesEnum.Login.name) { inclusive = true }
                    }
                    authVM.clearErrorMessage()
                }
            }
        }

        composable(PagesEnum.Register.name) {
            RegisterView(
                authenticationViewModel = authVM,
                context = context,
                navController = navController
            )
        }

        // ==========================================
        // 2. BOOK FLOW
        // ==========================================
        composable(PagesEnum.Books.name) {
            BookMainView(navController)
        }

        composable("BooksList") {
            BooksList(navController)
        }

        composable(PagesEnum.BookCreate.name) {
            BookCreateView(navController)
        }

        composable(PagesEnum.BookDetail.name + "/{bookId}") { backStack ->
            backStack.arguments?.getString("bookId")?.toIntOrNull()?.let { id ->
                BookDetailView(bookId = id, navController = navController)
            }
        }

        // ==========================================
        // 3. ITEM FLOW (TRANSAKSI)
        // ==========================================
        composable(
            route = "ItemsList/{bookId}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
            ItemsList(
                navController = navController,
                bookId = bookId
            )
        }

        // ==========================================
        // 4. WALLET FLOW
        // ==========================================
        composable(PagesEnum.Wallet.name) {
            WalletView(navController)
        }

        composable(PagesEnum.WalletCreate.name) {
            WalletAddEdit(navController)
        }

        composable(PagesEnum.WalletDetail.name + "/{walletId}") { backStack ->
            backStack.arguments?.getString("walletId")?.toIntOrNull()?.let { id ->
                WalletDetailView(walletId = id, navController = navController)
            }
        }

        composable(PagesEnum.WalletEdit.name + "/{walletId}") { backStack ->
            backStack.arguments?.getString("walletId")?.toIntOrNull()?.let { id ->
                WalletAddEdit(navController = navController, walletId = id)
            }
        }

        // ==========================================
        // 5. SAVING FLOW
        // ==========================================
        composable(PagesEnum.Saving.name) {
            val savingListViewModel: SavingListViewModel = viewModel(factory = SavingListViewModel.Factory)
            val savingFormViewModel: SavingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory)
            val token by savingFormViewModel.token.collectAsState(initial = "")

            SavingListViewWithNavBar(
                navController = navController,
                savingListViewModel = savingListViewModel,
                savingFormViewModel = savingFormViewModel,
                token = token
            )
        }

        composable(PagesEnum.CreateSaving.name) {
            val savingFormViewModel: SavingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory)
            val savingDetailViewModel: SavingDetailViewModel = viewModel(factory = SavingDetailViewModel.Factory)
            val token by savingFormViewModel.token.collectAsState(initial = "")

            SavingListFormView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                context = context,
                savingListFormViewModel = savingFormViewModel,
                navController = navController,
                savingDetailViewModel = savingDetailViewModel,
                token = token,
                userId = 1
            )
        }

        composable(PagesEnum.EditSaving.name) {
            val savingFormViewModel: SavingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory)
            val savingDetailViewModel: SavingDetailViewModel = viewModel(factory = SavingDetailViewModel.Factory)
            val token by savingFormViewModel.token.collectAsState(initial = "")

            SavingListFormView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                context = context,
                savingListFormViewModel = savingFormViewModel,
                navController = navController,
                savingDetailViewModel = savingDetailViewModel,
                token = token,
                userId = 1
            )
        }

        composable(PagesEnum.SavingDetail.name) {
            val savingDetailViewModel: SavingDetailViewModel = viewModel(factory = SavingDetailViewModel.Factory)
            val savingFormViewModel: SavingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory)
            val token by savingFormViewModel.token.collectAsState(initial = "")

            SavingListDetailView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                savingDetailViewModel = savingDetailViewModel,
                savingListFormViewModel = savingFormViewModel,
                navController = navController,
                token = token,
                userId = 1,
                savingId = 1,
                context = context
            )
        }

        composable(PagesEnum.AddAmount.name) {
            Text(
                text = "Add Amount Page (Under Construction)",
                modifier = Modifier.fillMaxSize()
            )
        }

        // ==========================================
        // 6. OTHER FEATURES
        // ==========================================
        composable(PagesEnum.ManageCategory.name) {
            ManageCategoryView(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(PagesEnum.Settings.name) {
            SettingView(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingListView(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Tabungan") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(PagesEnum.CreateSaving.name)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Saving")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Belum ada tabungan.\nTekan + untuk menambah.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { navController.navigate(PagesEnum.CreateSaving.name) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Buat Tabungan Baru")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingListViewWithNavBar(
    navController: NavHostController,
    savingListViewModel: SavingListViewModel,
    savingFormViewModel: SavingListFormViewModel,
    token: String
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            savingListViewModel.getAllSavings(token)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (token.isNotEmpty()) {
                    savingListViewModel.getAllSavings(token)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Tabungan") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(PagesEnum.CreateSaving.name)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Saving")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            when (savingListViewModel.savingListUIState) {
                is SavingListUIState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SavingListUIState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Gagal memuat data", color = MaterialTheme.colorScheme.error)
                        Button(
                            onClick = {
                                if (token.isNotEmpty()) {
                                    savingListViewModel.getAllSavings(token)
                                }
                            }
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is SavingListUIState.Success -> {
                    val savings = (savingListViewModel.savingListUIState as SavingListUIState.Success).savings
                    if (savings.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Belum ada tabungan.\nTekan + untuk menambah.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Button(
                                onClick = { navController.navigate(PagesEnum.CreateSaving.name) },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Buat Tabungan Baru")
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(savings) { saving ->
                                SavingListCard(
                                    saving = saving,
                                    navController = navController,
                                    savingListFormViewModel = savingFormViewModel,
                                    onDelete = { savingId ->
                                        // Implement delete function if needed
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
