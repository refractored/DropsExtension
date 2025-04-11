plugins {
    kotlin("jvm") version "2.0.20"
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "net.refractored"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.auxilor.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly(fileTree("libs"))
    compileOnly("com.github.refractored:BloodmoonReloaded:main-SNAPSHOT")
    compileOnly("com.github.refractored:HordesExtension:-SNAPSHOT")
    compileOnly("io.github.revxrsal:lamp.common:4.0.0-rc.9")
    compileOnly("io.github.revxrsal:lamp.bukkit:4.0.0-rc.9")
    compileOnly("io.github.revxrsal:lamp.brigadier:4.0.0-rc.9")
    compileOnly("com.willfp:eco:6.75.2")
//    compileOnly("com.willfp:libreforge:4.74.0")
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib", version = "2.1.0"))
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.shadowJar {
    relocate("revxrsal.commands", "net.refractored.libs.lamp")
}

tasks.build {

    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("extension.yml") {
        expand(props)
    }
}
