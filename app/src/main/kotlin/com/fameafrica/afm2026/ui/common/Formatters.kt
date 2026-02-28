package com.fameafrica.afm2026.ui.common

import java.text.NumberFormat
import java.util.Currency

fun formatCurrency(amount: Long, currencyCode: String = "EUR"): String {
    val format = NumberFormat.getCurrencyInstance()
    format.currency = Currency.getInstance(currencyCode)
    format.maximumFractionDigits = 0
    return format.format(amount)
}
