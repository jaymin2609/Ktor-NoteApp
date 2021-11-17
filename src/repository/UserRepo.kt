package com.noteapp.repository

import com.noteapp.data.model.User
import com.noteapp.db.table.UserTable
import com.noteapp.db.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class UserRepo {

    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert { tb ->
                tb[email] = user.email
                tb[name] = user.userName
                tb[hashPassword] = user.hashPass
            }
        }

    }

    suspend fun findUserByEmail(email: String) =
        dbQuery {

            UserTable.select {
                UserTable.email eq (email)
            }.map {
                rowToUser(it)
            }.singleOrNull()
        }

    private fun rowToUser(row: ResultRow?): User? {
        return if (row == null) {
            null
        } else {
            User(
                email = row[UserTable.email],
                userName = row[UserTable.name],
                hashPass = row[UserTable.hashPassword]
            )
        }
    }


}