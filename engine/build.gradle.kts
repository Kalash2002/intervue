// The local engine: HTTP API, sessions, store, runners. Layer rules are enforced by ArchitectureTest.
dependencies {
    // The language SPI exposes Variant, so packages compiled against the engine see it too.
    api(project(":shared-java:problem-format"))
    // The section SPI grades RunResults.
    api(project(":shared-java:run-protocol"))
    implementation(libs.sqlite.jdbc)
    implementation(libs.jackson.databind)
    testImplementation(libs.archunit)
}

tasks.test {
    // Content tests read the real problems in content/; they are inputs so content changes re-run them.
    systemProperty("intervue.content", rootProject.file("content").absolutePath)
    inputs.dir(rootProject.file("content/problems")).withPropertyName("problems")
}
