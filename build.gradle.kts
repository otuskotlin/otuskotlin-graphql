plugins {
    kotlin("jvm") version "1.5.0" apply false
    kotlin("plugin.serialization") version "1.5.0" apply false
    id("com.expediagroup.graphql") version "4.1.1" apply false
}

group = "ru.otus.otuskotlin"
version = "1.0-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }
}
