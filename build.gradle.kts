plugins {
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    java
}

group = "me.cookie"
version = ""

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }
}

dependencies {
    compileOnly(kotlin("stdlib", "1.6.0"))
    compileOnly(files("G:\\coding\\Test Servers\\TimeRewards\\plugins\\CookieCore.jar"))
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.0")
}

tasks{
    shadowJar{
        archiveClassifier.set("")
        project.configurations.implementation.get().isCanBeResolved = true
        configurations = listOf(project.configurations.implementation.get())
        destinationDirectory.set(file("G:\\coding\\Test Servers\\TimeRewards\\plugins"))
    }
}

java {
    withSourcesJar()
    withJavadocJar()

    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
