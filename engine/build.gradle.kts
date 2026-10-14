// The local engine: HTTP API, sessions, store, runners. Layer rules are enforced by ArchitectureTest.
dependencies {
    // The language SPI exposes Variant, so packages compiled against the engine see it too.
    api(project(":shared-java:problem-format"))
    // The section SPI grades RunResults.
    api(project(":shared-java:run-protocol"))
    testImplementation(libs.archunit)
}
