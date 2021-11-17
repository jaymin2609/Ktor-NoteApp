package com.noteapp.data.model

data class NoteRequest(
    val pageNumber: Long?,
) {
    fun isNull(): Boolean {
        if (pageNumber == null) {
            return true
        }
        return false
    }
}