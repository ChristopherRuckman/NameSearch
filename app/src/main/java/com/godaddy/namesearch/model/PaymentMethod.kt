package com.godaddy.namesearch.model

data class PaymentMethod(
    val name: String,
    val token: String,
    val lastFour: String?,
    val displayFormattedEmail: String?
)
