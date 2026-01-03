package com.example.todolistapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.AuthenticationStatusUIState
import com.example.todolistapp.viewModels.AuthenticationViewModel

// IMPORT VIEW UTAMA (BookViews.kt, ItemViews.kt, Auth Views ada di sini)
import com.example.todolistapp.views.* // Import komponen spesifik yang masih terpisah (jika belum digabung)
import com.example.todolistapp.views.components.book.BookDetailView
import com.example.todolistapp.views.components.book.BooksList
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
        // (Menggunakan BookMainView & BookCreateView dari BookViews.kt)
        // ==========================================
        composable(PagesEnum.Books.name) {
            BookMainView(navController)
        }

        // Ini untuk dropdown list buku (jika masih pakai komponen terpisah)
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
        // 3. ITEM FLOW (TRANSAKSI) - UPDATED
        // (Menggunakan ItemsList dari ItemViews.kt)
        // ==========================================
        composable(
            route = "ItemsList/{bookId}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0

            // Memanggil fungsi ItemsList yang ada di views/ItemViews.kt
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
        // 5. OTHER FEATURES
        // ==========================================
        composable(PagesEnum.ManageCategory.name) {
            ManageCategoryView(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(PagesEnum.Settings.name) {
            SettingView(navController)
        }

        composable(PagesEnum.Saving.name) {
            Text(
                text = "Halaman Saving (Under Construction)",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}