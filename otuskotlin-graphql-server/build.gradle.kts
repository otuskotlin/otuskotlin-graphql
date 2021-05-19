import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask

plugins {
    kotlin("jvm")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.expediagroup:graphql-kotlin-server:4.1.1")
    implementation("io.ktor:ktor-server-netty:1.5.4")
    implementation("io.ktor:ktor-jackson:1.5.4")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.expediagroup:graphql-kotlin-hooks-provider:4.1.1")

    testImplementation(kotlin("test-junit"))
    testImplementation("com.expediagroup:graphql-kotlin-ktor-client:4.1.1") {
        exclude("com.expediagroup", "graphql-kotlin-client-serialization")
    }
    testImplementation("com.expediagroup:graphql-kotlin-client-jackson:4.1.1")
    testImplementation("io.ktor:ktor-server-test-host:1.5.4")
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("ru.otus.otuskotlin.graphql.server.schema"))
}

val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateTestClientTask::class) {
    dependsOn(graphqlGenerateSDL)
    schemaFile.set(graphqlGenerateSDL.schemaFile)
    packageName.set("ru.otus.otuskotlin.graphql.server.client")
}
