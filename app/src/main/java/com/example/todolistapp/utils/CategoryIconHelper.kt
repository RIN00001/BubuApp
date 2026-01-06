package com.example.todolistapp.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {
    fun getIcon(iconKey: String?): ImageVector {
        return when (iconKey) {
            // --- Expense Icons ---
            "food" -> Icons.Default.Restaurant
            "transport" -> Icons.Default.DirectionsBus
            "shopping" -> Icons.Default.ShoppingCart
            "bill" -> Icons.Default.Receipt
            "entertainment" -> Icons.Default.Movie
            "health" -> Icons.Default.MedicalServices
            "education" -> Icons.Default.School
            "installment" -> Icons.Default.CreditCard
            "other" -> Icons.Default.Category

            // --- Income Icons ---
            "salary" -> Icons.Default.AttachMoney
            "bonus" -> Icons.Default.CardGiftcard
            "investment" -> Icons.Default.TrendingUp

            // --- Default Fallback ---
            else -> Icons.Default.Folder // Icon folder kuning default kamu
        }
    }
}