package com.example.todolistapp.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.uiStates.WalletDetailStatusUIState
import com.example.todolistapp.viewModels.WalletViewModel
import com.example.todolistapp.views.components.wallet._WalletDeletePopUp
import com.example.todolistapp.views.components.wallet._WalletDetails

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
            CenterAlignedTopAppBar(
                title = { Text("Wallet Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("<")
                    }
                }
            )
        }
    ) { padding ->

        when (detailState) {
            is WalletDetailStatusUIState.Loading,
            is WalletDetailStatusUIState.Start -> {
                CircularProgressIndicator(modifier = Modifier.padding(padding))
            }

            is WalletDetailStatusUIState.Success -> {
                val wallet = (detailState as WalletDetailStatusUIState.Success).data

                _WalletDetails(
                    wallet = wallet,
                    onSetDefault = {
                        walletViewModel.setDefault(wallet.id)
                        navController.popBackStack()
                    },
                    onEdit = {
                        navController.navigate(
                            "WalletEdit/${wallet.id}"
                        )
                    },
                    onDelete = {
                        showDeleteDialog = true
                    }
                )

                if (showDeleteDialog) {
                    _WalletDeletePopUp(
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
                Text("Failed to load wallet")
            }
        }
    }
}