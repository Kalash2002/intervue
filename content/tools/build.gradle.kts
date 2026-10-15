// Content tools: verify a problem three ways against the real toolchains (docs/contracts/problem-format.md).
plugins {
    application
}

dependencies {
    implementation(project(":engine"))
    implementation(project(":shared-java:run-protocol"))
    runtimeOnly(project(":languages:java:runner"))
}

application {
    mainClass = "si.intervue.content.Verify"
}

// ./gradlew :content:tools:verify [-Pids=hold-exposure,corridor-router]
tasks.register<JavaExec>("verify") {
    group = "verification"
    description = "Verifies content/problems three ways: starter fails, reference passes, naive times out."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "si.intervue.content.Verify"
    workingDir = rootDir
    args = (findProperty("ids") as String?)?.split(",") ?: emptyList()
}
