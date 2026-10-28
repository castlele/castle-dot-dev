import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "2.4.21"
    kotlin("plugin.compose") version "2.4.21"
    id("org.jetbrains.compose") version "1.12.2"
}

val composeVersion = "1.12.2"
val markdownVersion = "0.46.0"

// Markdown in content/ becomes Kotlin strings, so articles need no fetch.
// shortcut: a JVM string constant tops out at 64 KB, so an article must stay
// below that; move the text to a resource file if one ever grows past it.
val generateContent by tasks.registering {
    val content = layout.projectDirectory.dir("content")
    val out = layout.buildDirectory.dir("generated/content")
    val includeDrafts = providers.gradleProperty("includeDrafts").isPresent
    inputs.dir(content)
    inputs.property("includeDrafts", includeDrafts)
    outputs.dir(out)
    doLast {
        fun literal(file: File) =
            file.readText().replace("\r", "")
                .replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("$", "\\$").replace("\n", "\\n")
                .let { "\"$it\"" }
        val articles = content.dir("articles").asFile
            .listFiles { f -> f.extension == "md" }!!
            .sortedBy { it.name }
            .joinToString(",\n") {
                "        \"${it.nameWithoutExtension}\" to ${literal(it)}"
            }
        val file = out.get().file("dev/castledot/GeneratedContent.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package dev.castledot

            internal object GeneratedContent {
                const val includeDrafts = $includeDrafts
                val about = ${literal(content.file("about.md").asFile)}
                val articles = mapOf(
            $articles,
                )
            }
            """.trimIndent() + "\n",
        )
    }
}

kotlin {
    // The JVM target only runs the logic tests without a browser.
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "site"
        browser {
            commonWebpackConfig { outputFileName = "site.js" }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateContent)
        }
        commonMain.dependencies {
            // The Compose plugin also runs on the JVM target and generates
            // the resource accessors in commonMain.
            implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
            implementation(
                "org.jetbrains.compose.components:components-resources:$composeVersion",
            )
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        wasmJsMain.dependencies {
            implementation("org.jetbrains.compose.foundation:foundation:$composeVersion")
            implementation("org.jetbrains.compose.ui:ui:$composeVersion")
            implementation(
                "com.mikepenz:multiplatform-markdown-renderer:$markdownVersion",
            )
            implementation(
                "com.mikepenz:multiplatform-markdown-renderer-code:$markdownVersion",
            )
        }
    }
}

compose.resources {
    packageOfResClass = "dev.castledot.resources"
}
