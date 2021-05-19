package com.godaddy.namesearch.model

import com.godaddy.namesearch.User

data class LoginResponse(
    val auth: Auth,
    val user: User
)
