package ru.otus.otuskotlin.graphql.server.schema

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

// src/main/resources/META-INF/services/com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
class CustomSchemaGeneratorHooksProvider : SchemaGeneratorHooksProvider {
    override fun hooks(): SchemaGeneratorHooks =
        CustomSchemaGeneratorHooks()
}

class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {
    override fun willGenerateGraphQLType(type: KType): GraphQLType? =
        when (type.classifier as? KClass<*>) {
            UUID::class -> UUIDType
            else -> null
        }
}

val UUIDType: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("A type representing a formatted java.util.UUID")
    .coercing(UUIDCoercing())
    .build()

class UUIDCoercing : Coercing<UUID, String> {
    override fun serialize(dataFetcherResult: Any?): String =
        dataFetcherResult.toString()

    override fun parseValue(input: Any?): UUID =
        UUID.fromString(input.toString())

    override fun parseLiteral(input: Any?): UUID =
        UUID.fromString((input as? StringValue)?.value)
}