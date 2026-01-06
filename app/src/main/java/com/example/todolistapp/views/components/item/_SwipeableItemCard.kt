package com.example.todolistapp.views.components.item

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistapp.models.ItemModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableItemCard(
    item: ItemModel,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit // Fungsi ini akan dipanggil oleh Swipe maupun Tombol
) {
    // State untuk fitur Geser (Swipe)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete() // Panggil dialog hapus
                    false // Return false agar card tidak langsung hilang sebelum konfirmasi
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false, // Hanya bisa geser kanan ke kiri
        backgroundContent = {
            val color by animateColorAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent,
                label = "colorAnimation"
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 1f,
                label = "scaleAnimation"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(12.dp))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.scale(scale)
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ----------------------------------------------------
                // KIRI: ICON PANAH & NAMA TRANSAKSI
                // ----------------------------------------------------
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isExpense = item.type == "EXPENSE"
                    Icon(
                        imageVector = if (isExpense) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = item.type,
                        tint = if (isExpense) Color.Red else Color(0xFF4CAF50),
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if(isExpense) "Pengeluaran" else "Pemasukan",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // ----------------------------------------------------
                // KANAN: NOMINAL & ACTION BUTTONS (EDIT + DELETE)
                // ----------------------------------------------------
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val amountText = "Rp ${item.amount.toLong()}"
                    Text(
                        text = if (item.type == "EXPENSE") "- $amountText" else "+ $amountText",
                        fontWeight = FontWeight.Bold,
                        color = if (item.type == "EXPENSE") Color.Red else Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 8.dp) // Beri jarak dengan tombol
                    )

                    // Tombol Edit
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp) // Ukuran diperkecil sedikit agar rapi
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // [BARU] Tombol Delete
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete, // Icon Sampah
                            contentDescription = "Delete",
                            tint = Color(0xFFE57373), // Warna Merah Muda (Soft Red)
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}