plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.1"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "me.zombie_striker"
version = "2.1.1-SNAPSHOT"
description = "QualityArmoryVehicles"

java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.viaversion.com")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://repo.citizensnpcs.co/")
}

dependencies {
    implementation("com.github.cryptomorin:XSeries:8.5.0.1")
    implementation("net.jodah:expiringmap:0.5.10")
    implementation("org.codemc.worldguardwrapper:worldguardwrapper:1.2.0-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.9.3") // only used by paper
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
    compileOnly("us.myles:viaversion:3.2.1")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("me.zombie_striker:QualityArmory:2.0.4")
    compileOnly("com.github.TownyAdvanced:Towny:0.97.5.1")
    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("org.jetbrains:annotations:23.0.0")
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
    }

    build {
        dependsOn("shadowJar")
    }

    runServer {
        minecraftVersion("1.18.1")
    }

    processResources {
        expand("version" to version)
    }
}

