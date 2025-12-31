package com.example.todolistapp.views.components.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Wallet" else "Add Wallet",
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFE6E6))
                .padding(padding)
        ) {
            when {
                isEditMode && detailState is WalletDetailStatusUIState.Success -> {
                    val wallet = (detailState as WalletDetailStatusUIState.Success).data

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        _WalletAddForm(
                            initialWallet = wallet,
                            onSubmit = { name, balance ->
                                walletViewModel.updateWallet(wallet.id, name, balance)
                            }
                        )
                    }
                }

                !isEditMode -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        _WalletAddForm(
                            onSubmit = { name, balance ->
                                walletViewModel.createWallet(name, balance)
                            }
                        )
                    }
                }

                else -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Show loading or error
            when (mutationState) {
                is WalletMutationStatusUIState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is WalletMutationStatusUIState.Failed -> {
                    Text(
                        text = "Error: ${(mutationState as WalletMutationStatusUIState.Failed).error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {}
            }
        }
    }
}
