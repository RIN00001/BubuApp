package com.example.todolistapp.views
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
