package com.example.todolistapp.viewModels

import com.example.todolistapp.models.SavingModel

/**
 * Object ini berfungsi sebagai "Tempat Penitipan Data" sementara.
 * Digunakan saat kita ingin mengedit sebuah item:
 * 1. Layar List menyimpan data SavingModel ke sini.
 * 2. Layar Form mengambil data dari sini untuk ditampilkan di input field.
 */
object SharedDataViewModel {
    var editingSavingModel: SavingModel? = null
}