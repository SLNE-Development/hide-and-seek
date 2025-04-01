import dev.slne.surf.surfapi.gradle.util.registerRequired
import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("io.freefair.lombok") version "8.10"
    id("dev.slne.surf.surfapi.gradle.paper-plugin") version "1.21.4+"
}

group = "dev.slne"
version = findProperty("version") as String

repositories {
    maven("https://jitpack.io/")
}

dependencies {
    paperLibrary("com.mojang:datafixerupper:8.0.16")
    paperLibrary("com.github.Querz:NBT:6.1")

    compileOnly("com.github.NEZNAMY:TAB-API:5.2.0")
}

surfPaperPluginApi {
    mainClass("dev.slne.hideandnseek.PaperMain")
    authors.add("SLNE Development")

    serverDependencies {
        registerRequired("TAB")
    }

    runServer {
        withSurfApiBukkit()
        jvmArgs("-XX:+AllowEnhancedClassRedefinition -Dpaper.playerconnection.keepalive=9999999")
        javaLauncher = javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}