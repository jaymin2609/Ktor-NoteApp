package com.noteapp.repository

import com.noteapp.data.model.Note
import com.noteapp.db.table.NoteTable
import com.noteapp.db.DatabaseFactory
import com.noteapp.utility.NOTE_PAGING_COUNT
import org.jetbrains.exposed.sql.*

class NoteRepo {
    var offset = 1
    suspend fun addNote(note: Note, email: String) {
        DatabaseFactory.dbQuery {
            NoteTable.insert { tb ->
                tb[userEmail] = email
                tb[noteTitle] = note.noteTitle
                tb[description] = note.description
                tb[date] = note.date
            }
        }
    }

    suspend fun updateNote(note: Note, email: String) =
        DatabaseFactory.dbQuery {
            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
                }
            ) { tb ->
                tb[noteTitle] = note.noteTitle
                tb[description] = note.description
                tb[date] = note.date
            }

        }

    suspend fun deleteNote(id: Int, email: String) =
        DatabaseFactory.dbQuery {
            NoteTable.deleteWhere { NoteTable.userEmail.eq(email) and NoteTable.id.eq(id) }
        }

    suspend fun getAllNotes(email: String, pageCount: Long = 0) =
        DatabaseFactory.dbQuery {
            NoteTable.select {
                NoteTable.userEmail.eq(email)
            }
                .limit(NOTE_PAGING_COUNT, NOTE_PAGING_COUNT * (pageCount - 1))
                .orderBy(NoteTable.id to SortOrder.ASC)
                .mapNotNull { rowToNote(it) }
        }

    private fun rowToNote(row: ResultRow?): Note? {
        return if (row == null) {
            null
        } else {
            Note(
                id = row[NoteTable.id],
                noteTitle = row[NoteTable.noteTitle],
                description = row[NoteTable.description],
                date = row[NoteTable.date]
            )
        }
    }
}