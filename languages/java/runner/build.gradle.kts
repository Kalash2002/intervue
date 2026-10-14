// The Java language package (docs/contracts/language-contract.md). Tests run the real JDK.
dependencies {
    testImplementation(project(":shared-java:run-protocol"))
}

tasks.test {
    // The kit is plain source copied into each session's work folder, so tests compile it themselves.
    systemProperty("intervue.testkit", file("../testkit").absolutePath)
    inputs.dir("../testkit").withPropertyName("testkit")
}

// The kit is Java source shipped to users; it follows the same format as the rest.
spotless {
    java { target("src/**/*.java", "../testkit/**/*.java") }
}
