import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.expediagroup:graphql-kotlin-ktor-client:4.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
}

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
    schemaFile.set(project(":otuskotlin-graphql-server").layout.buildDirectory.file("schema.graphql"))
    packageName.set("ru.otus.otuskotlin.graphql.client.queries")
    serializer.set(GraphQLSerializer.KOTLINX)
}
