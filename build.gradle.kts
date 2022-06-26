plugins {
    kotlin("jvm") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    `maven-publish`
}

group = "kr.kro.minestar"
version = "1.0.0"

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io/")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
            name = "sonatype-oss-snapshots"
        }
        maven(url = "https://jitpack.io/")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        compileOnly("net.kyori:adventure-api:4.10.1")
        compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")

        //project_TL

        //MineStar
        implementation("com.github.MineStarAS:Utility-API:1.2.6")

//        implementation(files("C:\\Users\\MineStar\\Desktop\\MC Server folder\\libs\\Utility-API-1.2.6.jar"))
    }
}
