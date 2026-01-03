package com.example.todolistapp.enums

enum class PagesEnum {

    // AUTH
    Login,
    Register,

    // MAIN TABS (BOTTOM NAV)
    Books,      // MAIN SCREEN (Book dashboard)
    Wallet,
    Saving,
    Settings,

    // BOOK FLOWS (DETAIL / CRUD)
    BookDetail,
    BookCreate,
    BookEdit,

    // WALLET FLOWS (DETAIL / CRUD)
    WalletDetail,
    WalletCreate,
    WalletEdit,

    // --- TAMBAHAN FITUR KAMU (ITEM & CATEGORY) ---
    CreateItem,
    EditItem,       // (Opsional, buat jaga-jaga kalau nanti ada edit)
    ManageCategory,
    CreateCategory  // (Opsional)
}