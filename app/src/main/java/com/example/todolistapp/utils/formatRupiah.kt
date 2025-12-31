package com.example.todolistapp.utils

import java.text.NumberFormat
import java.util.*

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}
