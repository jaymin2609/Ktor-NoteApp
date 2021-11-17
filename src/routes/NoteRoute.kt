package com.noteapp.routes

import com.noteapp.data.model.*
import com.noteapp.repository.NoteRepo
import com.noteapp.utility.*
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import java.io.File
import java.lang.Exception


const val NOTES = "$API_VERSION/notes"
const val CREATE_NOTES = "$NOTES/create"
const val UPDATE_NOTES = "$NOTES/update"
const val DELETE_NOTES = "$NOTES/delete"


fun Route.noteRoutes(
    noteRepo: NoteRepo,
) {
    authenticate("jwt") {
        post(CREATE_NOTES) {
            val note = call.receive<Note>()

            if (note.isNull()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(false, null, ERROR_MISSING_ARGS)
                )
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                noteRepo.addNote(note, email)
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(true, null, MSG_NOTE_ADDED)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, null, e.message ?: ERROR_SOME_PROBLEM_OCCURRED)
                )
            }
        }

        get("$NOTES/{pageCount}") {
            try {
                val pageCount = call.parameters["pageCount"]!!.toLong()
                val email = call.principal<User>()!!.email
                val notes = noteRepo.getAllNotes(email, pageCount)

                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(true, notes, MSG_SUCCESSFULLY)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, emptyList<Note>(), e.message ?: ERROR_SOME_PROBLEM_OCCURRED)
                )
            }
        }

        post(NOTES) {
            try {
                val email = call.principal<User>()!!.email
                val noteRequest = call.receive<NoteRequest>()

                if (noteRequest.isNull()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(false, null, ERROR_MISSING_ARGS)
                    )
                    return@post
                }

                val notes = noteRepo.getAllNotes(email, noteRequest.pageNumber!!)

                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(true, NoteResponse(notes), MSG_SUCCESSFULLY)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, emptyList<Note>(), e.message ?: ERROR_SOME_PROBLEM_OCCURRED)
                )
            }
        }

        post(UPDATE_NOTES) {
            val note = call.receive<Note>()

            if (note.isNull()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(false, null, ERROR_MISSING_ARGS)
                )
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                val result = noteRepo.updateNote(note, email)
                if (result == 1) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(true, null, MSG_UPDATED_SUCCESSFULLY)
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(false, null, ERROR_MISSING_ARGS)
                    )
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, null, e.message ?: ERROR_SOME_PROBLEM_OCCURRED)
                )
            }
        }

        delete(DELETE_NOTES) {
            val noteId = try {
                call.request.queryParameters["id"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, null, e.message ?: ERROR_SOME_PROBLEM_OCCURRED)
                )
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                val result = noteRepo.deleteNote(noteId, email)
                if (result == 1) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(true, null, MSG_DELETED_SUCCESSFULLY)
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(false, null, ERROR_MISSING_ARGS)
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(false, null, e.message ?: ERROR_SOME_PROBLEM_OCCURRED)
                )
            }

        }

    }


}