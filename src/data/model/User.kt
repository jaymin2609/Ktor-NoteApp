package com.noteapp.data.model

import io.ktor.auth.Principal

data class User(
    val email: String,
    val hashPass: String,
    val userName: String
):Principal