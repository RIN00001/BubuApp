package com.example.todolistapp.utils

import java.text.NumberFormat
import java.util.*

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

fun formatRupiah(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

fun formatRupiah(amount: Int): String {
    return formatRupiah(amount.toLong())
}
