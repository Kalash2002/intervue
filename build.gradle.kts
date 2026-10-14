// Shared conventions for every Java project: toolchain 21, warnings as errors, google-java-format,
// JUnit, JaCoCo. Per-project build files add only their dependencies and coverage rules.
plugins {
    alias(libs.plugins.spotless) apply false
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "jacoco")
    apply(plugin = "com.diffplug.spotless")

    repositories { mavenCentral() }

    extensions.configure<JavaPluginExtension> {
        toolchain { languageVersion = JavaLanguageVersion.of(21) }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
    }

    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        lineEndings = com.diffplug.spotless.LineEnding.UNIX
        java { googleJavaFormat(rootProject.libs.versions.googleJavaFormat.get()) }
    }

    dependencies {
        "testImplementation"(platform(rootProject.libs.junit.bom))
        "testImplementation"(rootProject.libs.junit.jupiter)
        "testImplementation"(rootProject.libs.assertj)
        "testRuntimeOnly"(rootProject.libs.junit.launcher)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }
}
