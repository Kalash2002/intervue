// The Java language package (docs/contracts/language-contract.md). Tests run the real JDK.
dependencies {
    implementation(project(":engine"))
    implementation(libs.jackson.databind)
    testImplementation(project(":shared-java:run-protocol"))
}

// The kit, the benchmark and language.json ship inside the jar; the package copies them out at run time.
tasks.processResources {
    from("../testkit")
    from("../calibration") { into("calibration") }
    from("../language.json")
}

tasks.test {
    // The kit is plain source copied into each session's work folder, so tests compile it themselves.
    systemProperty("intervue.testkit", file("../testkit").absolutePath)
    inputs.dir("../testkit").withPropertyName("testkit")
}

// The kit is Java source shipped to users; it follows the same format as the rest.
spotless {
    java { target("src/**/*.java", "../testkit/**/*.java", "../calibration/**/*.java") }
}
