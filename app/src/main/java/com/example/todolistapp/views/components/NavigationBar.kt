package com.example.bubuapp.views.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.todolistapp.enums.PagesEnum

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NavigationBarPreview() {
    NavigationBar(
        navController = NavHostController(LocalContext.current)
    )
}

@Composable
fun NavigationBar(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val items = listOf(
        NavItem(
            label = "Home",
            route = PagesEnum.Home.name,
            icon = Icons.Default.Home
        ),
        NavItem(
            label = "Books",
            route = PagesEnum.BookList.name,
            icon = Icons.Default.Book
        ),
        NavItem(
            label = "Wallet",
            route = PagesEnum.WalletList.name,
            icon = Icons.Default.Wallet
        )
    )

    NavigationBar(
        containerColor = Color(0xFFFFE6E6)
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(PagesEnum.Home.name) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF7469B6),
                    selectedTextColor = Color(0xFF7469B6),
                    indicatorColor = Color(0xFFE1AFD1),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

private data class NavItem(
    val label: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
