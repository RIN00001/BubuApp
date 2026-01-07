package com.example.todolistapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.bubuapp.views.components.NavigationBar
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.models.WalletModel
import com.example.todolistapp.uiStates.WalletListStatusUIState
import com.example.todolistapp.utils.formatRupiah
import com.example.todolistapp.viewModels.WalletViewModel
import com.example.todolistapp.views.components.wallet.SwipeableWalletCard
import com.example.todolistapp.views.components.wallet._WalletDeletePopUp

@Composable
fun WalletView(
    navController: NavHostController,
    walletViewModel: WalletViewModel = viewModel(factory = WalletViewModel.Factory)
) {
    val listState by walletViewModel.listState.collectAsState()
    var hideValues by remember { mutableStateOf(false) }
    var walletToDelete by remember { mutableStateOf<WalletModel?>(null) }

    LaunchedEffect(Unit) {
        walletViewModel.fetchWallets()
    }

    Scaffold(
        bottomBar = {
            NavigationBar(navController)
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Eye button to toggle visibility
                FloatingActionButton(
                    onClick = { hideValues = !hideValues },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (hideValues) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle visibility",
                        tint = Color(0xFF9B8FC7)
                    )
                }

                // Add button
                FloatingActionButton(
                    onClick = {
                        navController.navigate(PagesEnum.WalletCreate.name)
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Wallet",
                        tint = Color(0xFF9B8FC7)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Purple Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF9B8FC7))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Wallet",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    when (listState) {
                        is WalletListStatusUIState.Success -> {
                            val wallets = (listState as WalletListStatusUIState.Success).data
                            val totalNet = wallets.sumOf { it.balance }
                            val debt = 0.0 // Placeholder for now

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Total Net
                                Column {
                                    Text(
                                        text = "Total Net",
                                        fontSize = 16.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Text(
                                        text = if (hideValues) "Rp****" else formatRupiah(totalNet),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                // Debt
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Debt",
                                        fontSize = 16.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Text(
                                        text = if (hideValues) "Rp****" else formatRupiah(debt),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        else -> {
                            // Loading or error state
                            Text(
                                text = "Total Net",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "Rp0",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Wallet List
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (listState) {
                    is WalletListStatusUIState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                        )
                    }

                    is WalletListStatusUIState.Success -> {
                        val wallets = (listState as WalletListStatusUIState.Success).data

                        if (wallets.isEmpty()) {
                            Text(
                                text = "No wallets yet. Create one!",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(wallets) { wallet ->
                                    SwipeableWalletCard(
                                        wallet = wallet,
                                        hideValues = hideValues,
                                        canDelete = wallets.size > 1,
                                        onClick = {
                                            navController.navigate(
                                                PagesEnum.WalletDetail.name + "/${wallet.id}"
                                            )
                                        },
                                        onDelete = {
                                            if (wallets.size > 1) {
                                                walletToDelete = wallet
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is WalletListStatusUIState.Failed -> {
                        Text(
                            text = "Failed to load wallets",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> {}
                }
            }
        }

        // Delete Dialog
        walletToDelete?.let { wallet ->
            _WalletDeletePopUp(
                walletName = wallet.name,
                onConfirm = {
                    walletViewModel.deleteWallet(wallet.id)
                    walletToDelete = null
                },
                onDismiss = {
                    walletToDelete = null
                }
            )
        }
    }
}
