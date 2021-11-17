package com.noteapp.data.model

data class LoginRequest(
    val email: String?,
    val password: String?
) {
    fun isNull(): Boolean {
        if (email == null || password == null) {
            return true
        }
        return false
    }
}