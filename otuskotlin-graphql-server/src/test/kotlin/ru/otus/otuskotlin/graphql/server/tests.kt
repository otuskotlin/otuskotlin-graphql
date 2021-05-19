package ru.otus.otuskotlin.graphql.server

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import io.ktor.application.Application
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking
import ru.otus.otuskotlin.graphql.server.client.GetNote
import ru.otus.otuskotlin.graphql.server.client.getnote.NoteVersion
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class QueryTest {
    @Test
    fun `get note`() = withTestApplication(Application::graphqlServer) {
        val client = GraphQLKtorClient(URL("http:///graphql"), client)
        val variables = GetNote.Variables("c59706f1-1a5c-4678-97fb-651d40873b1b", 1)
        val query = GetNote(variables)

        val result = runBlocking { client.execute(query) }

        with(result) {
            assertNotNull(data, errors.toString()) {
                assertEquals("c59706f1-1a5c-4678-97fb-651d40873b1b", it.note.id)
                assertEquals("foo", it.note.name)
                assertEquals(1, it.note.versions.size)
                assertContains(
                    it.note.versions,
                    NoteVersion("8456f939-eec5-4ef4-899f-a873961bc855", "bar")
                )
            }
        }
    }
}