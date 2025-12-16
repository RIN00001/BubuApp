package com.example.todolistapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.viewModels.AuthenticationViewModel
import com.example.todolistapp.views.LoginView
import com.example.todolistapp.views.RegisterView
import com.example.todolistapp.views.components.wallet.WalletAddEdit
import com.example.todolistapp.views.components.wallet.WalletListView
import com.example.todolistapp.views.WalletDetailView
import com.example.todolistapp.views.components.book.BookDetailView
import com.example.todolistapp.views.components.book.BooksList
import com.example.todolistapp.views.BookCreateView
import androidx.compose.ui.platform.LocalContext
import com.example.todolistapp.uiStates.AuthenticatonStatusUIState
import com.example.todolistapp.views.BookMainView
import com.example.todolistapp.views.WalletView

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    val authVM: AuthenticationViewModel =
        viewModel(factory = AuthenticationViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = PagesEnum.Login.name,
        modifier = modifier
    ) {

        // ---------- AUTH ----------
        composable(PagesEnum.Login.name) {
            LoginView(
                authenticationViewModel = authVM,
                context = context,
                navController = navController
            )

            if (authVM.authenticationStatus is AuthenticatonStatusUIState.Success) {
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

        // ---------- BOOKS (MAIN TAB) ----------
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
            backStack.arguments?.getString("bookId")?.toIntOrNull()?.let {
                BookDetailView(it, navController)
            }
        }

        // ---------- WALLET ----------
        composable(PagesEnum.Wallet.name) {
            WalletView(navController)
        }

        composable(PagesEnum.WalletDetail.name + "/{walletId}") { backStack ->
            backStack.arguments?.getString("walletId")?.toIntOrNull()?.let {
                WalletDetailView(it, navController)
            }
        }

        composable(PagesEnum.WalletCreate.name) {
            WalletAddEdit(navController)
        }

        composable(PagesEnum.WalletEdit.name + "/{walletId}") { backStack ->
            backStack.arguments?.getString("walletId")?.toIntOrNull()?.let {
                WalletAddEdit(navController, it)
            }
        }

        // ---------- PLACEHOLDERS ----------
        composable(PagesEnum.Saving.name) { }
        composable(PagesEnum.Settings.name) { }
    }
}

