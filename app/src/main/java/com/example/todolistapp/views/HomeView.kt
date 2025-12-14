package com.example.todolistapp.views
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.bubuapp.views.components.NavigationBar

@Composable
fun HomeView(
    navController: NavHostController
) {
    Scaffold(
        bottomBar = {
            NavigationBar(navController = navController)
        }
    ) { padding ->
        Text(
            text = "Home (Book Dashboard)",
            modifier = Modifier.padding(padding)
        )
    }
}
