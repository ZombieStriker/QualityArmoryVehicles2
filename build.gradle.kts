import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "me.zombie_striker"
version = "2.3.2"
description = "QualityArmoryVehicles"

java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.viaversion.com")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://repo.citizensnpcs.co/")
    maven("https://mvn.lumine.io/repository/maven-public/") {
        metadataSources {
            artifact()
        }
    }
    maven("https://repo.codemc.io/repository/maven-public/") {
        metadataSources {
            artifact()
        }
    }
}

dependencies {
    // Libraries
    implementation("com.github.cryptomorin:XSeries:8.7.1")
    implementation("net.jodah:expiringmap:0.5.10")
    implementation("org.codemc.worldguardwrapper:worldguardwrapper:1.2.0-SNAPSHOT")
    implementation("dev.triumphteam:triumph-gui:3.1.2")
    compileOnly("org.jetbrains:annotations:23.0.0")

    // API
    compileOnly("net.kyori:adventure-api:4.10.1")
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")

    // Compatibilities
    compileOnly("us.myles:viaversion:3.2.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    compileOnly("me.zombie_striker:QualityArmory:2.0.6")
    compileOnly("com.github.TownyAdvanced:Towny:0.98.1.9")
    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("com.ticxo.modelengine:api:R2.5.0")
    compileOnly("org.maxgamer:QuickShop:5.1.0.6")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    jar {
        archiveVersion.set("")
    }

    shadowJar {
        archiveVersion.set("")
        archiveClassifier.set("")

        relocate("com.cryptomorin.xseries", "me.zombie_striker.qav.util.xseries")
        relocate("net.jodah.expiringmap", "me.zombie_striker.qav.util.expiringmap")
        relocate("org.codemc.worldguardwrapper", "me.zombie_striker.qav.hooks.worldguard")
        relocate("dev.triumphteam.gui", "me.zombie_striker.qav.gui")
    }

    build {
        dependsOn("shadowJar")
    }

    runServer {
        minecraftVersion("1.18.2")
    }

    processResources {
        expand(mapOf("version" to version, "commit" to gitDescribe))
    }
}

val gitDescribe: String by lazy {
    val stdout = ByteArrayOutputStream()
    rootProject.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
}
