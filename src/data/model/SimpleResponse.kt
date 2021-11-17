package com.noteapp.data.model

data class SimpleResponse<T>(
    val success: Boolean,
    val data: T,
    val message: String
)