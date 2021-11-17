package com.noteapp.data.model

data class RegisterRequest(
    val email: String?,
    val name: String?,
    val password: String?
) {
    fun isNull(): Boolean {
        if (email == null || name == null || password == null) {
            return true
        }
        return false
    }
}