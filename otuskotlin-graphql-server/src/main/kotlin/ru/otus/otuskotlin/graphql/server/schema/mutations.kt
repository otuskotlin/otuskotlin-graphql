package ru.otus.otuskotlin.graphql.server.schema

import com.expediagroup.graphql.server.operations.Mutation
import ru.otus.otuskotlin.graphql.server.KtorGraphQLContext
import java.util.*

class RootMutation : Mutation {
    suspend fun createNote(data: NoteData, context: KtorGraphQLContext): Note {
        val note = context.noteDao.create(data.name)
        val version = context.noteVersionDao.create(note.id, data.value)
        return Note(note, NoteVersion(version))
    }

    suspend fun renameNote(id: UUID, name: String, context: KtorGraphQLContext): Note {
        return context.noteDao.rename(id, name).let { Note(it) }
    }

    suspend fun updateNote(id: UUID, value: String, context: KtorGraphQLContext): Note {
        val note = context.noteDao.get(id)
        val version = context.noteVersionDao.create(note.id, value)
        return Note(note, NoteVersion(version))
    }

    suspend fun removeNote(id: UUID, context: KtorGraphQLContext): Boolean {
        context.noteVersionDao.removeAll(id)
        return try {
            context.noteDao.remove(id)
            true
        } catch (e: NoSuchElementException) {
            false
        }
    }
}

class NoteData(
    val name: String,
    val value: String
)