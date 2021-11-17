package com.noteapp.data.model

import com.google.gson.annotations.SerializedName

data class JwtTokenResponse(
    @SerializedName("token") val jwtToken: String
)