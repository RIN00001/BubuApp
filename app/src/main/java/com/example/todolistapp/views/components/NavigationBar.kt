package com.example.bubuapp.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.todolistapp.R
import com.example.todolistapp.enums.PagesEnum

@Preview(showBackground = true)
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
        NavItem("Books", PagesEnum.Books.name, Icons.Default.Book, Icons.Default.Book),
        NavItem("Wallet", PagesEnum.Wallet.name, Icons.Outlined.Wallet, Icons.Filled.Wallet),
        NavItem("Saving", PagesEnum.Saving.name, Icons.Outlined.Savings, Icons.Filled.Savings),
        NavItem("More", PagesEnum.Settings.name, Icons.Outlined.MoreHoriz, Icons.Filled.MoreHoriz)
    )

    NavigationBar(
        containerColor = Color(0xFFAD88C6),
        contentColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = false
                            }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color(0xFF7D5A8A),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    item.selectedIcon,
                                    contentDescription = item.label,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Icon(
                                item.unselectedIcon,
                                contentDescription = item.label,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color.White else Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private data class NavItem(
    val label: String,
    val route: String,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)

