package ru.otus.otuskotlin.graphql.server.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.*

data class NoteVersionDto(
    val id: UUID,
    val value: String
)

interface NoteVersionDao {
    fun list(noteId: UUID): Flow<NoteVersionDto>
    suspend fun get(noteId: UUID, id: UUID): NoteVersionDto
    suspend fun create(noteId: UUID, value: String): NoteVersionDto
    suspend fun removeAll(noteId: UUID)

    class Fake(
        private val items: MutableMap<UUID, MutableList<NoteVersionDto>>
    ) : NoteVersionDao {
        constructor(vararg items: Pair<NoteDto, NoteVersionDto>) : this(
            items.fold(mutableMapOf()) { acc, (note, version) ->
                acc.computeIfAbsent(note.id) { mutableListOf() }.add(version)
                acc
            }
        )

        override fun list(noteId: UUID): Flow<NoteVersionDto> =
            items[noteId].orEmpty().asFlow()

        override suspend fun get(noteId: UUID, id: UUID): NoteVersionDto =
            items[noteId].orEmpty().first { it.id == id }

        override suspend fun create(noteId: UUID, value: String): NoteVersionDto =
            NoteVersionDto(UUID.randomUUID(), value).also {
                items.computeIfAbsent(noteId) { mutableListOf() }.add(0, it)
            }

        override suspend fun removeAll(noteId: UUID) {
            items.remove(noteId)
        }
    }
}