package com.example.todolistapp.navigation

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.BookListStatusUIState
import com.example.todolistapp.viewModels.AuthenticationViewModel
import com.example.todolistapp.viewModels.BookViewModel
import com.example.todolistapp.views.LoginView
import com.example.todolistapp.views.RegisterView
import com.example.todolistapp.views.home.BookOnboardingView
import com.example.todolistapp.views.home.BooksList
import androidx.compose.ui.platform.LocalContext
import com.example.todolistapp.views.HomeView
import com.example.todolistapp.views.WalletDetailView
import com.example.todolistapp.views.components.home.BookDetailView
import com.example.todolistapp.views.components.wallet.WalletAddEdit
import com.example.todolistapp.views.components.wallet.WalletListView

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val authenticationViewModel: AuthenticationViewModel =
        viewModel(factory = AuthenticationViewModel.Factory)

    val bookViewModel: BookViewModel =
        viewModel(factory = BookViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = PagesEnum.Login.name,
        modifier = modifier
    ) {

        // ---------- LOGIN ----------
        composable(PagesEnum.Login.name) {
            LoginView(
                authenticationViewModel = authenticationViewModel,
                context = context,
                navController = navController
            )
        }

        // ---------- REGISTER ----------
        composable(PagesEnum.Register.name) {
            RegisterView(
                authenticationViewModel = authenticationViewModel,
                context = context,
                navController = navController
            )
        }

        // ---------- HOME GATE ----------
        composable(PagesEnum.Home.name) {
            val listState by bookViewModel.listState.collectAsState()

            LaunchedEffect(Unit) {
                bookViewModel.fetchBooks()
            }

            when (listState) {
                is BookListStatusUIState.Start,
                is BookListStatusUIState.Loading -> {
                    // loading state
                }

                is BookListStatusUIState.Success -> {
                    val books = (listState as BookListStatusUIState.Success).data

                    if (books.isEmpty()) {
                        BookOnboardingView(
                            bookViewModel = bookViewModel,
                            onDone = {
                                navController.navigate(PagesEnum.Home.name) {
                                    popUpTo(PagesEnum.Home.name) { inclusive = true }
                                }
                            }
                        )
                    } else {
                        HomeView(navController = navController)
                    }
                }

                is BookListStatusUIState.Failed -> {
                    BookOnboardingView(
                        bookViewModel = bookViewModel,
                        onDone = {
                            navController.navigate(PagesEnum.Home.name) {
                                popUpTo(PagesEnum.Home.name) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // ---------- BOOK ----------
        composable(PagesEnum.BookList.name) {
            BooksList(navController = navController)
        }

        composable(PagesEnum.BookDetail.name + "/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments
                ?.getString("bookId")
                ?.toIntOrNull()

            if (bookId != null) {
                BookDetailView(
                    bookId = bookId,
                    navController = navController
                )
            }
        }

        // ---------- WALLET ----------
        composable(PagesEnum.WalletList.name) {
            WalletListView(navController = navController)
        }

        composable(PagesEnum.WalletDetail.name + "/{walletId}") { backStackEntry ->
            val walletId = backStackEntry.arguments
                ?.getString("walletId")
                ?.toIntOrNull()

            if (walletId != null) {
                WalletDetailView(
                    walletId = walletId,
                    navController = navController
                )
            }
        }

        composable(PagesEnum.WalletCreate.name) {
            WalletAddEdit(navController = navController)
        }

        composable(PagesEnum.WalletEdit.name + "/{walletId}") { backStackEntry ->
            val walletId = backStackEntry.arguments
                ?.getString("walletId")
                ?.toIntOrNull()

            WalletAddEdit(
                navController = navController,
                walletId = walletId
            )
        }
    }
}
