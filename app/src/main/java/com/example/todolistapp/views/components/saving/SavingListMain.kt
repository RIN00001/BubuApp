package com.example.todolistapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bubuapp.views.components.NavigationBar
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.navigation.SavingListView
import com.example.todolistapp.viewModels.AuthenticationViewModel
import com.example.todolistapp.viewModels.HomeViewModel
import com.example.todolistapp.viewModels.SavingDetailViewModel
import com.example.todolistapp.viewModels.SavingListFormViewModel
import com.example.todolistapp.viewModels.SharedDataViewModel

@Composable
fun SavingListApp(
    navController: NavHostController = rememberNavController()
) {
    val localContext = LocalContext.current

    NavHost(navController = navController, startDestination = PagesEnum.Login.name) {
        composable(route = PagesEnum.Login.name) {
            val authViewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
            LoginView(
                authenticationViewModel = authViewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                navController = navController,
                context = localContext
            )
        }

        composable(route = PagesEnum.Register.name) {
            val authViewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
            RegisterView(
                authenticationViewModel = authViewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                navController = navController,
                context = localContext
            )
        }

        composable(route = PagesEnum.CreateSaving.name) {
            val savingFormViewModel: SavingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory)
            val savingDetailViewModel: SavingDetailViewModel = viewModel(factory = SavingDetailViewModel.Factory)
            val token by savingFormViewModel.token.collectAsState(initial = "")

            LaunchedEffect(token) {
                if (token.isNotEmpty()) {
                    savingFormViewModel.checkNullFormValues()
                }
            }

            SavingListFormView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                context = localContext,
                savingListFormViewModel = savingFormViewModel,
                navController = navController,
                savingDetailViewModel = savingDetailViewModel,
                token = token,
                userId = 1
            )
        }

        composable(route = PagesEnum.EditSaving.name) {
            val savingFormViewModel: SavingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory)
            val savingDetailViewModel: SavingDetailViewModel = viewModel(factory = SavingDetailViewModel.Factory)
            val token by savingFormViewModel.token.collectAsState(initial = "")

            LaunchedEffect(Unit) {
                SharedDataViewModel.editingSavingModel?.let { savingModel ->
                    savingFormViewModel.loadEditData(savingModel)
                }
            }

            LaunchedEffect(token) {
                if (token.isNotEmpty()) {
                    savingFormViewModel.checkNullFormValues()
                }
            }

            SavingListFormView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                context = localContext,
                savingListFormViewModel = savingFormViewModel,
                navController = navController,
                savingDetailViewModel = savingDetailViewModel,
                token = token,
                userId = 1
            )
        }

        composable(route = PagesEnum.SavingDetail.name) {
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
                context = localContext
            )
        }

        composable(route = PagesEnum.AddAmount.name) {
            val savingFormViewModel: SavingListFormViewModel = viewModel(factory = SavingListFormViewModel.Factory)
            val token by savingFormViewModel.token.collectAsState(initial = "")
            val savingModel = SharedDataViewModel.currentSavingModel

            if (savingModel != null) {
                SavingAddAmountView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    context = localContext,
                    navController = navController,
                    savingListFormViewModel = savingFormViewModel,
                    homeViewModel = viewModel(factory = HomeViewModel.Factory),
                    token = token,
                    userId = 1,
                    savingModel = savingModel
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: No saving selected",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { navController.navigate(PagesEnum.Saving.name) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Back to Saving")
                    }
                }
            }
        }

        composable(route = PagesEnum.Books.name) {
            Scaffold(
                bottomBar = {
                    NavigationBar(navController)
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    HomeView(
                        navController = navController
                    )
                }
            }
        }

        composable(route = PagesEnum.Saving.name) {
            Scaffold(
                bottomBar = {
                    NavigationBar(navController)
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    SavingListView(
                        navController = navController
                    )
                }
            }
        }
    }
}
