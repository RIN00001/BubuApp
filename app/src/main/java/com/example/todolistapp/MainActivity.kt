package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.utils.TokenManager
import com.example.todolistapp.viewModels.AuthenticationViewModel
import com.example.todolistapp.viewModels.CategoryViewModel
import com.example.todolistapp.viewModels.CreateItemViewModel
import com.example.todolistapp.views.CategoryDetailView
import com.example.todolistapp.views.CreateItemView
import com.example.todolistapp.views.HomeView
import com.example.todolistapp.views.LoginView
import com.example.todolistapp.views.ManageCategoryView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)

        setContent {
            TodoListAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val token = TokenManager.getToken()

                    // Start Destination
                    val startDestination = if (!token.isNullOrEmpty()) PagesEnum.Home.name else PagesEnum.Login.name

                    NavHost(navController = navController, startDestination = startDestination) {

                        // 1. LOGIN
                        composable(route = PagesEnum.Login.name) {
                            val loginViewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
                            LoginView(authenticationViewModel = loginViewModel, navController = navController, context = context)
                        }

                        // 2. HOME (SEKARANG MENAMPILKAN LIST KATEGORI)
                        composable(route = PagesEnum.Home.name) {
                            // Home sekarang pakai CategoryViewModel
                            val catViewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory)
                            HomeView(navController = navController, viewModel = catViewModel)
                        }

                        // 3. CATEGORY DETAIL (LIST ITEM PER KATEGORI)
                        composable(
                            route = "category_items/{categoryId}/{categoryName}",
                            arguments = listOf(
                                navArgument("categoryId") { type = NavType.IntType },
                                navArgument("categoryName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
                            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Detail"

                            // Kita butuh HomeViewModel untuk load Items
                            val homeViewModel: com.example.todolistapp.viewModels.HomeViewModel = viewModel(factory = com.example.todolistapp.viewModels.HomeViewModel.Factory)

                            CategoryDetailView(
                                navController = navController,
                                viewModel = homeViewModel,
                                categoryId = categoryId,
                                categoryName = categoryName
                            )
                        }

                        // 4. CREATE / EDIT TRANSACTION
                        // Menerima optional categoryId (jika ditambah dari dalam folder)
                        composable(
                            route = "create?categoryId={categoryId}",
                            arguments = listOf(
                                navArgument("categoryId") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ) {
                            val createViewModel: CreateItemViewModel = viewModel(factory = CreateItemViewModel.Factory)
                            CreateItemView(navController = navController, viewModel = createViewModel)
                        }

                        // 5. MANAGE CATEGORIES (Opsional, buat tambah/edit nama kategori)
                        composable(route = "manage_categories") {
                            val categoryViewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory)
                            ManageCategoryView(
                                onBackClick = { navController.popBackStack() },
                                viewModel = categoryViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}