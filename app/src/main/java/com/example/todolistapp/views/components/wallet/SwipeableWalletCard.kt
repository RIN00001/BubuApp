package com.example.todolistapp.views.components.wallet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.models.WalletModel
import com.example.todolistapp.utils.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableWalletCard(
    wallet: WalletModel,
    hideValues: Boolean,
    canDelete: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    if (canDelete) {
                        onDelete()
                    }
                    false // Don't actually dismiss, show dialog instead
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            if (canDelete) {
                val color by animateColorAsState(
                    when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF0000)
                        else -> Color.Transparent
                    },
                    label = "background color"
                )

                val scale by animateFloatAsState(
                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.3f else 1f,
                    label = "icon scale"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color, RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "delete",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.scale(scale)
                    )
                }
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Wallet Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = wallet.name,
                            tint = Color(0xFF4CAF50)
                        )
                    }

                    // Wallet Name
                    Text(
                        text = wallet.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Balance and IDR
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (hideValues) "Rp****" else formatRupiah(wallet.balance),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (!hideValues) {
                        Text(
                            text = "IDR(1.0)",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

