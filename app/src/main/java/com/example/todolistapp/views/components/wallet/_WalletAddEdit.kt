package com.example.todolistapp.views.components.wallet


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todolistapp.uiStates.WalletDetailStatusUIState
import com.example.todolistapp.uiStates.WalletMutationStatusUIState
import com.example.todolistapp.viewModels.WalletViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletAddEdit(
    navController: NavHostController,
    walletId: Int? = null,
    walletViewModel: WalletViewModel = viewModel(factory = WalletViewModel.Factory)
) {
    val detailState by walletViewModel.detailState.collectAsState()
    val mutationState by walletViewModel.mutationState.collectAsState()

    val isEditMode = walletId != null

    LaunchedEffect(walletId) {
        if (isEditMode) {
            walletViewModel.fetchWalletDetail(walletId!!)
        }
    }

    LaunchedEffect(mutationState) {
        if (mutationState is WalletMutationStatusUIState.Success) {
            walletViewModel.resetMutationState()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(if (isEditMode) "Edit Wallet" else "Add Wallet")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("<")
                    }
                }
            )
        }
    ) { padding ->

        when {
            isEditMode && detailState is WalletDetailStatusUIState.Success -> {
                val wallet = (detailState as WalletDetailStatusUIState.Success).data

                _WalletAddForm(
                    initialWallet = wallet,
                    onSubmit = { name, balance ->
                        walletViewModel.updateWallet(wallet.id, name, balance)
                    }
                )
            }

            !isEditMode -> {
                _WalletAddForm(
                    onSubmit = { name, balance ->
                        walletViewModel.createWallet(name, balance)
                    }
                )
            }

            else -> {
                CircularProgressIndicator()
            }
        }
    }
}
