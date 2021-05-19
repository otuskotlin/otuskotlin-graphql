package ru.otus.otuskotlin.graphql.server.schema

import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import ru.otus.otuskotlin.graphql.server.KtorGraphQLContext
import ru.otus.otuskotlin.graphql.server.dao.NoteDto
import ru.otus.otuskotlin.graphql.server.dao.NoteVersionDto
import java.util.*

class RootQuery : Query {
    suspend fun note(id: UUID, context: KtorGraphQLContext): Note =
        context.noteDao.get(id).let { Note(it) }

    suspend fun notes(first: Int, after: Int, context: KtorGraphQLContext): List<Note> =
        context.noteDao.list().drop(after).take(first).map { Note(it) }.toList()
}

class Note(
    val id: UUID,
    val name: String,
    private val current: NoteVersion? = null
) {
    constructor(dto: NoteDto) : this(dto.id, dto.name)
    constructor(dto: NoteDto, current: NoteVersion) : this(dto.id, dto.name, current)

    suspend fun current(context: KtorGraphQLContext): NoteVersion =
        current ?: context.noteVersionDao.list(this.id).first().let { NoteVersion(it) }

    suspend fun version(id: UUID, context: KtorGraphQLContext): NoteVersion =
        context.noteVersionDao.get(this.id, id).let { NoteVersion(it) }

    suspend fun versions(first: Int, after: Int, context: KtorGraphQLContext): List<NoteVersion> =
        context.noteVersionDao.list(this.id).drop(after).take(first).map { NoteVersion(it) }.toList()
}

class NoteVersion(
    val id: UUID,
    val value: String
) {
    constructor(dto: NoteVersionDto) : this(dto.id, dto.value)
}