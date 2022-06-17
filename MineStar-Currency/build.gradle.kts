group = "kr.kro.minestar"
version = "1.0.0"

val copyPath = File("C:\\Users\\MineStar\\Desktop\\MC Server folder\\MCserver 1.18.1 - vanilla\\plugins")

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
        archivePath.delete()
        doLast {
            // jar file copy
            copy {
                from(archiveFile)
                into(if (File(copyPath, archiveFileName.get()).exists()) copyPath else copyPath)
            }
        }
    }
}