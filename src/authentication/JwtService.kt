package com.noteapp.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.noteapp.data.model.User
import com.noteapp.utility.JWT_TOKEN_EXPIRY_TIME
import java.util.*

class JwtService {


    private val issuer = "noteServer"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject("NoteAuthentication")
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + JWT_TOKEN_EXPIRY_TIME))
            .withClaim("email", user.email)
            .sign(algorithm)
    }
}