package com.noteapp.data.model

data class Note(
    val id: Int,
    val noteTitle: String,
    val description: String,
    val date: Long
) {
    fun isNull(): Boolean {

        if (id == null || noteTitle == null || description == null || date == null) {
            return true
        }
        return false
    }
}