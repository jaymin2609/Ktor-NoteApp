package com.noteapp.routes

import com.noteapp.authentication.JwtService
import com.noteapp.data.model.*
import com.noteapp.repository.UserRepo
import com.noteapp.utility.*
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import java.lang.Exception


const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"


fun Route.userRoutes(
    userRepo: UserRepo,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {

    post(REGISTER_REQUEST) {
        val registerRequest = call.receive<RegisterRequest>()

        if (registerRequest.isNull()) {
            call.respond(
                HttpStatusCode.BadRequest,
                SimpleResponse(false, null, ERROR_MISSING_ARGS)
            )
            return@post
        }

        try {
            val user = User(registerRequest.email!!, hashFunction(registerRequest.password!!), registerRequest.name!!)
            userRepo.addUser(user)
            call.respond(
                HttpStatusCode.OK,
                SimpleResponse(true, JwtTokenResponse(jwtService.generateToken(user)), MSG_REGISTERED_SUCCESSFULLY)
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, null, e.message ?: ERROR_SOME_PROBLEM_OCCURRED)
            )
        }


    }

    post(LOGIN_REQUEST) {
        val loginRequest = call.receive<LoginRequest>()

        if (loginRequest.isNull()) {
            call.respond(
                HttpStatusCode.BadRequest,
                SimpleResponse(false, null, ERROR_MISSING_ARGS)
            )
            return@post
        }

        try {
            val user = userRepo.findUserByEmail(loginRequest.email!!)
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(false, null, MSG_USER_NOT_EXIST)
                )
                return@post
            } else {
                if (user.hashPass == hashFunction(loginRequest.password!!)) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(true, JwtTokenResponse(jwtService.generateToken(user)), MSG_LOGGED_SUCCESSFULLY)
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(false, null, MSG_USER_NOT_EXIST)
                    )
                }
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, null, e.message ?: ERROR_SOME_PROBLEM_OCCURRED)
            )
        }
    }
}