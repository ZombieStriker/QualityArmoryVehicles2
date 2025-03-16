import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "me.zombie_striker"
version = "2.4.1"
description = "QualityArmoryVehicles"

java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.citizensnpcs.co/")
    maven("https://repo.glaremasters.me/repository/towny/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.viaversion.com")
    maven("https://repo.codemc.io/repository/maven-public/") {
        metadataSources {
            artifact()
        }
    }
    maven("https://jitpack.io")
}

dependencies {
    // Libraries
    implementation("com.github.cryptomorin:XSeries:13.1.0")
    implementation("net.jodah:expiringmap:0.5.11")
    implementation("org.codemc.worldguardwrapper:worldguardwrapper:1.2.1-SNAPSHOT")
    implementation("dev.triumphteam:triumph-gui:3.1.10")
    compileOnly("org.jetbrains:annotations:26.0.1")

    // API
    compileOnly("net.kyori:adventure-api:4.18.0")
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

    // Compatibilities
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("me.zombie_striker:QualityArmory:2.0.18")
    compileOnly("com.palmergames.bukkit.towny:towny:0.101.0.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.ticxo.modelengine:api:R3.2.0")
    compileOnly("org.maxgamer:QuickShop:5.1.2.5")
    compileOnly("com.viaversion:viaversion-api:5.2.1")
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
