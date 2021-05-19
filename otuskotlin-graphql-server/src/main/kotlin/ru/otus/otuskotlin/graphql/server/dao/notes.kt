package ru.otus.otuskotlin.graphql.server.dao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.*

data class NoteDto(
    val id: UUID,
    val name: String
)

interface NoteDao {
    fun list(): Flow<NoteDto>
    suspend fun get(id: UUID): NoteDto
    suspend fun create(name: String): NoteDto
    suspend fun rename(id: UUID, name: String): NoteDto
    suspend fun remove(id: UUID)

    class Fake(
        private val items: MutableList<NoteDto>
    ) : NoteDao {
        constructor(vararg items: NoteDto) : this(items.toMutableList())

        override fun list(): Flow<NoteDto> =
            items.asFlow()

        override suspend fun get(id: UUID): NoteDto =
            items.first { it.id == id }

        override suspend fun create(name: String): NoteDto =
            NoteDto(UUID.randomUUID(), name).also { items.add(it) }

        override suspend fun rename(id: UUID, name: String): NoteDto =
            items.first { it.id == id }.copy(name = name).also { note ->
                items.removeIf { it.id == id }
                items.add(note)
            }

        override suspend fun remove(id: UUID) {
            items.removeIf { it.id == id }
        }
    }
}