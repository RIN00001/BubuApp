package com.example.todolistapp.views.components.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.enums.PagesEnum
import com.example.todolistapp.uiStates.WalletListStatusUIState
import com.example.todolistapp.viewModels.WalletViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletListView(
    navController: NavHostController,
    walletViewModel: WalletViewModel = viewModel(factory = WalletViewModel.Factory)
) {
    val listState by walletViewModel.listState.collectAsState()

    LaunchedEffect(Unit) {
        walletViewModel.fetchWallets()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Wallet") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(PagesEnum.WalletCreate.name)
                }
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            when (listState) {
                is WalletListStatusUIState.Loading,
                is WalletListStatusUIState.Start -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                }

                is WalletListStatusUIState.Success -> {
                    val wallets = (listState as WalletListStatusUIState.Success).data

                    WalletInfo(wallets = wallets)

                    wallets.forEach { wallet ->
                        WalletCard(
                            wallet = wallet,
                            onClick = {
                                navController.navigate(
                                    PagesEnum.WalletDetail.name + "/${wallet.id}"
                                )
                            }
                        )
                    }
                }

                is WalletListStatusUIState.Failed -> {
                    Text(
                        text = "Failed to load wallets",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}