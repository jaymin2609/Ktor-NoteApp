package com.noteapp

import com.noteapp.authentication.JwtService
import com.noteapp.authentication.hash
import com.noteapp.data.model.SimpleResponse
import com.noteapp.db.DatabaseFactory
import com.noteapp.repository.NoteRepo
import com.noteapp.repository.UserRepo
import com.noteapp.routes.noteRoutes
import com.noteapp.routes.uploadRoutes
import com.noteapp.routes.userRoutes
import com.noteapp.utility.ERROR_JWT_TOKEN_EXPIRE
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    DatabaseFactory.init()

    val userRepo = UserRepo()
    val noteRepo = NoteRepo()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }


    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "NoteServer"
            validate { call ->
                val payload = call.payload
                val email = payload.getClaim("email").asString()
                if (email != "") {
                    val user = userRepo.findUserByEmail(email)
                    if (user != null) {
                        return@validate user
                    }
                    null
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    SimpleResponse(false, null, ERROR_JWT_TOKEN_EXPIRE)
                )
            }
        }
    }
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }

    }

    install(ContentNegotiation) {
        gson {}
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
        static ("/images"){
            files("uploads")
        }
        userRoutes(userRepo, jwtService, hashFunction)
        noteRoutes(noteRepo)
        uploadRoutes()
    }
}

data class MySession(val count: Int = 0)

