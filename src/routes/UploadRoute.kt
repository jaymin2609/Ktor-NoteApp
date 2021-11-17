package com.noteapp.routes

import com.noteapp.data.model.*
import com.noteapp.utility.*
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.Route
import io.ktor.routing.post
import java.io.File
import java.lang.Exception


const val UPLOAD_FILE = "$API_VERSION/upload"


fun Route.uploadRoutes() {
    authenticate("jwt") {
        post(UPLOAD_FILE) {
            try {
                val multipartData = call.receiveMultipart()
                var fileRequest = FileRequest()
                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name!! == "fileName") {
                                fileRequest.fileName = part.value
                            }
                        }
                        is PartData.FileItem -> {
                            if (part.name!! == "file") {
                                val fileName = part.originalFileName as String
                                val fileBytes = part.streamProvider().readBytes()
                                fileRequest.file = fileBytes
                                fileRequest.fileName = fileName
                            }
                        }
                        else -> {
                            fileRequest = FileRequest()
                        }
                    }
                }

                if (fileRequest.isNull()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(false, null, ERROR_MISSING_ARGS)
                    )
                    return@post
                } else {
                    val root = File("uploads")
                    root.mkdir()
                    if (root.exists()) {
                        File("${root.name}/${fileRequest.fileName}").writeBytes(fileRequest.file!!)
                        call.respond(
                            HttpStatusCode.OK, SimpleResponse(
                                true, "${fileRequest.fileName} is uploaded to 'uploads/${fileRequest.fileName}'",
                                MSG_SUCCESSFULLY
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.Conflict,
                            SimpleResponse(false, null, ERROR_FOLDER_NOT_EXISTS)
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


}