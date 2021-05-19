package com.godaddy.namesearch.model

data class Domain(
    val name: String,
    val price: String,
    val productId: Int,
    var selected: Boolean = false
)
