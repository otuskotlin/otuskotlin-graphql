package ru.otus.otuskotlin.graphql.server

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.generator.toSchema
import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.server.execution.GraphQLServer
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import graphql.GraphQL
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import ru.otus.otuskotlin.graphql.server.dao.NoteDao
import ru.otus.otuskotlin.graphql.server.dao.NoteDto
import ru.otus.otuskotlin.graphql.server.dao.NoteVersionDao
import ru.otus.otuskotlin.graphql.server.dao.NoteVersionDto
import ru.otus.otuskotlin.graphql.server.schema.CustomSchemaGeneratorHooks
import ru.otus.otuskotlin.graphql.server.schema.RootMutation
import ru.otus.otuskotlin.graphql.server.schema.RootQuery
import java.util.*

class KtorGraphQLRequestParser : GraphQLRequestParser<ApplicationCall> {
    override suspend fun parseRequest(request: ApplicationCall): GraphQLServerRequest =
        request.receive()
}

class KtorGraphQLContextFactory(
    val noteDao: NoteDao,
    val noteVersionDao: NoteVersionDao
) : GraphQLContextFactory<KtorGraphQLContext, ApplicationCall> {
    override suspend fun generateContext(request: ApplicationCall): KtorGraphQLContext =
        KtorGraphQLContext(request, noteDao, noteVersionDao)
}

data class KtorGraphQLContext(
    val call: ApplicationCall,
    val noteDao: NoteDao,
    val noteVersionDao: NoteVersionDao
) : GraphQLContext

fun Application.graphqlServer() {
    val graphQLSchema = toSchema(
        config = SchemaGeneratorConfig(
            supportedPackages = listOf("ru.otus.otuskotlin.graphql.server.schema"),
            hooks = CustomSchemaGeneratorHooks()
        ),
        queries = listOf(TopLevelObject(RootQuery())),
        mutations = listOf(TopLevelObject(RootMutation()))
    )

    val fakeNote = NoteDto(
        UUID.fromString("c59706f1-1a5c-4678-97fb-651d40873b1b"),
        "foo"
    )
    val fakeVersion = NoteVersionDto(
        UUID.fromString("8456f939-eec5-4ef4-899f-a873961bc855"),
        "bar"
    )

    val graphQLServer = GraphQLServer(
        requestParser = KtorGraphQLRequestParser(),
        contextFactory = KtorGraphQLContextFactory(
            NoteDao.Fake(fakeNote),
            NoteVersionDao.Fake(fakeNote to fakeVersion)
        ),
        requestHandler = GraphQLRequestHandler(
            graphQL = GraphQL.newGraphQL(graphQLSchema).build()
        )
    )

    install(ContentNegotiation) {
        jackson {
            registerKotlinModule()
            findAndRegisterModules()
        }
    }

    routing {
        post("/graphql") {
            call.respond(graphQLServer.execute(call)!!)
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        graphqlServer()
    }.start(wait = true)
}