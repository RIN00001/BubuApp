package com.example.todolistapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.viewModels.AuthenticationViewModel
import com.example.todolistapp.viewModels.HomeViewModel
import com.example.todolistapp.viewModels.TodoDetailViewModel
import com.example.todolistapp.viewModels.TodoListFormViewModel

@Composable
fun TodoListApp(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    todoListFormViewModel: TodoListFormViewModel = viewModel(factory = TodoListFormViewModel.Factory),
    authenticationViewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory),
    todoDetailViewModel: TodoDetailViewModel = viewModel(factory = TodoDetailViewModel.Factory)
) {
    val localContext = LocalContext.current
    val token = homeViewModel.token.collectAsState()

    NavHost(navController = navController, startDestination = if (token.value != "Unknown" && token.value != "") {
        PagesEnum.Home.name
    } else {
        PagesEnum.Login.name
    }) {
        composable(route = PagesEnum.Login.name) {
            LoginView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                authenticationViewModel = authenticationViewModel,
                navController = navController,
                context = localContext
            )
        }

        composable(route = PagesEnum.Register.name) {
            RegisterView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                authenticationViewModel = authenticationViewModel,
                navController = navController,
                context = localContext
            )
        }

        composable(route = PagesEnum.Home.name) {
            HomeView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                homeViewModel = homeViewModel,
                navController = navController,
                token.value,
                localContext,
                todoDetailViewModel
            )
        }

        composable(route = PagesEnum.CreateTodo.name) {
            TodoListFormView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                context = localContext,
                todoListFormViewModel =  todoListFormViewModel,
                navController = navController,
                todoDetailViewModel = todoDetailViewModel,
                token = token.value
            )
        }

        composable(route = PagesEnum.TodoDetail.name) {
            TodoListDetailView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                navController = navController,
                todoDetailViewModel,
                token.value,
                localContext,
                todoListFormViewModel
            )
        }
    }
}