package com.godaddy.namesearch

data class PaymentMethod(
    val name: String,
    val token: String,
    val lastFour: String?,
    val displayFormattedEmail: String?
)
