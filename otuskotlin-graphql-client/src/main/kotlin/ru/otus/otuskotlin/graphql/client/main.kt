package ru.otus.otuskotlin.graphql.client

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import ru.otus.otuskotlin.graphql.client.queries.GetNote
import java.net.URL

suspend fun main() {
    val client = GraphQLKtorClient(URL("http://localhost:8080/graphql"))
    val result = client.execute(GetNote(GetNote.Variables("c59706f1-1a5c-4678-97fb-651d40873b1b")))
    println(result)
}