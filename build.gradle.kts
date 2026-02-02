plugins {
    id("com.diffplug.spotless") version "8.2.1" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            googleJavaFormat("1.33.0").aosp()
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
            targetExclude("**/build/**", "**/generated-sources/**", "**/generated/**")
        }
    }
}

tasks.register<Copy>("copyGitHooks") {
    description = "Copies git hooks from project directory to .git/hooks"
    group = "git"

    val gitDir = File("$rootDir/.git")

    onlyIf { gitDir.exists() }

    from("$rootDir/git/hooks")
    into("${gitDir.path}/hooks")

    filePermissions {
        user { read = true; write = true; execute = true }

        group { read = true; execute = true }

        other { read = true; execute = true }
    }

    doLast {
        println("Git hooks installed successfully into ${gitDir.path}/hooks")
        file("$rootDir/git/hooks").listFiles()?.forEach {
            println("   -> Enabled hook: ${it.name}")
        }
    }
}

tasks.maybeCreate("prepareKotlinBuildScriptModel").dependsOn("copyGitHooks")
