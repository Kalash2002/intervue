// The local engine: HTTP API, sessions, store, runners. Layer rules are enforced by ArchitectureTest.
dependencies {
    // The language SPI exposes Variant, so packages compiled against the engine see it too.
    api(project(":shared-java:problem-format"))
    // The section SPI grades RunResults.
    api(project(":shared-java:run-protocol"))
    implementation(libs.sqlite.jdbc)
    implementation(libs.jackson.databind)
    testImplementation(libs.archunit)
    // Session tests run real code through the real packages; main code never sees them (ArchitectureTest).
    testRuntimeOnly(project(":languages:java:runner"))
    rootProject.subprojects.filter { it.path.matches(Regex(":sections:[^:]+:java")) }
        .forEach { testRuntimeOnly(it) }
}

tasks.test {
    // Content tests read the real problems in content/; they are inputs so content changes re-run them.
    systemProperty("intervue.content", rootProject.file("content").absolutePath)
    inputs.dir(rootProject.file("content/problems")).withPropertyName("problems")
}

// The distributable engine the shell starts with `java -jar engine.jar`: engine.jar plus lib/ holding
// every dependency and the installed packages, found through the manifest Class-Path. A separate
// jar task, because the packages depend on the engine's own jar.
val bundledPackages by configurations.creating
// Every section package (settings.gradle.kts finds them by folder) runs in tests and ships in the dist.
val sectionPackages = rootProject.subprojects.filter { it.path.matches(Regex(":sections:[^:]+:java")) }
dependencies {
    bundledPackages(project(":languages:java:runner"))
    sectionPackages.forEach { bundledPackages(it) }
}
val engineLibs = files(configurations.runtimeClasspath, bundledPackages)
    .filter { !it.startsWith(layout.buildDirectory.get().asFile) }
val engineJar by tasks.registering(Jar::class) {
    archiveFileName = "engine.jar"
    destinationDirectory = layout.buildDirectory.dir("engine-jar")
    from(sourceSets.main.get().output)
    inputs.files(engineLibs)
    doFirst {
        manifest.attributes(
            "Main-Class" to "si.intervue.engine.api.EngineMain",
            "Class-Path" to engineLibs.joinToString(" ") { "lib/" + it.name },
        )
    }
}
tasks.register<Sync>("engineDist") {
    from(engineJar)
    from(engineLibs) { into("lib") }
    into(layout.buildDirectory.dir("engine"))
}

// DoD coverage rule: at least 80% of lines in the use-case packages. The engine has no `domain` or
// `application` packages by those names; these are their equivalents (api and runners are adapters).
tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    violationRules {
        rule {
            element = "PACKAGE"
            includes = listOf(
                "si.intervue.engine.session",
                "si.intervue.engine.session.edits",
                "si.intervue.engine.health",
                "si.intervue.engine.content",
                "si.intervue.engine.store",
            )
            limit {
                counter = "LINE"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}
tasks.check { dependsOn(tasks.jacocoTestCoverageVerification) }
