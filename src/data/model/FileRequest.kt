package com.noteapp.data.model

data class FileRequest(
    var fileName: String? = null,
    var file: ByteArray? = null
) {
    fun isNull(): Boolean {
        if (fileName == null || file == null) {
            return true
        }
        return false
    }
}