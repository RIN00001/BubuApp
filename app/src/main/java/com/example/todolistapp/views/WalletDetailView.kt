package com.example.todolistapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.WalletDetailStatusUIState
import com.example.todolistapp.utils.formatRupiah
import com.example.todolistapp.viewModels.WalletViewModel
import com.example.todolistapp.views.components.wallet._WalletDeletePopUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailView(
    walletId: Int,
    navController: NavHostController,
    walletViewModel: WalletViewModel = viewModel(factory = WalletViewModel.Factory)
) {
    val detailState by walletViewModel.detailState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(walletId) {
        walletViewModel.fetchWalletDetail(walletId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wallet Details",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9B8FC7)
                )
            )
        },
        floatingActionButton = {
            when (detailState) {
                is WalletDetailStatusUIState.Success -> {
                    val wallet = (detailState as WalletDetailStatusUIState.Success).data
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(PagesEnum.WalletEdit.name + "/${wallet.id}")
                        },
                        containerColor = Color(0xFFAD88C6),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Wallet",
                            tint = Color.White
                        )
                    }
                }
                else -> {}
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFE6E6))
                .padding(padding)
        ) {
            when (detailState) {
                is WalletDetailStatusUIState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }

                is WalletDetailStatusUIState.Success -> {
                    val wallet = (detailState as WalletDetailStatusUIState.Success).data

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Wallet Info Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = wallet.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = formatRupiah(wallet.balance),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF7469B6),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                if (wallet.isDefault) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = Color(0xFFE8F5E9),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Default Wallet",
                                            color = Color(0xFF4CAF50),
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Set as Default button (if not default)
                        if (!wallet.isDefault) {
                            Button(
                                onClick = {
                                    walletViewModel.setDefault(wallet.id)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFAD88C6)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Set as Default",
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        // Delete button
                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFFF0000)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Delete Wallet",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    // Delete Dialog
                    if (showDeleteDialog) {
                        _WalletDeletePopUp(
                            walletName = wallet.name,
                            onConfirm = {
                                walletViewModel.deleteWallet(wallet.id)
                                showDeleteDialog = false
                                navController.popBackStack()
                            },
                            onDismiss = {
                                showDeleteDialog = false
                            }
                        )
                    }
                }

                is WalletDetailStatusUIState.Failed -> {
                    Text(
                        text = "Failed to load wallet details",
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
}

