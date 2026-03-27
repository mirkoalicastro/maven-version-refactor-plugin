import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.13.1"
    id("org.jetbrains.changelog") version "2.2.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.7"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project

val platformType: String by project
val platformVersion: String by project

group = pluginGroup
version = pluginVersion

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(platformType, platformVersion)
        pluginVerifier()
    }
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.7")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.mockk:mockk:1.13.12")
}

detekt {
    config.setFrom("./detekt-config.yml")
    buildUponDefaultConfig = true
}

intellijPlatform {
    pluginConfiguration {
        name = pluginName
        version = pluginVersion
        description =
            provider {
                file("./README.md").readText().lines().run {
                    val start = "<!-- Plugin description -->"
                    val end = "<!-- Plugin description end -->"
                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md")
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n")
            }
        changeNotes =
            provider {
                changelog.renderItem(changelog.getLatest(), Changelog.OutputType.HTML)
            }
        ideaVersion {
            sinceBuild = pluginSinceBuild
            if (pluginUntilBuild.isNotEmpty()) {
                untilBuild = pluginUntilBuild
            }
        }
    }
    pluginVerification {
        ides {
            recommended()
        }
    }
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        channels = listOf(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
    }
}

kover {
    reports {
        total {
            verify {
                rule {
                    bound {
                        minValue = 100
                    }
                }
            }
        }
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    withType<Detekt> {
        jvmTarget = "17"
    }
    test {
        useJUnitPlatform()
    }
    publishPlugin {
        dependsOn("patchChangelog")
    }
}
