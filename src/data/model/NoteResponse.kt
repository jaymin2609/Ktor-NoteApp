package com.noteapp.data.model

import com.google.gson.annotations.SerializedName

data class NoteResponse(
    @SerializedName("notes") val note: List<Note>,
    @SerializedName("count") val count: Int = note.size
)