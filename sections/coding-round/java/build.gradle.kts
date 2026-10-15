// Jar named for the section: every section project is called "java", so the default lib/java.jar
// would be overwritten by the next section in :engine:engineDist.
base.archivesName = "coding-round"

// The coding-round section package: schema checks, tests grader, scoring rule, prompts
// (docs/contracts/section-contract.md). One project: grader and scoring ship together and are not
// reused apart, so PLAN.md 5.2's grader/ and scoring/ split would only add build files.
dependencies {
    implementation(project(":engine"))
    implementation(libs.jackson.databind)
}

// section.json and the prompts ship inside the jar under sections/<id>/: every section jar is on one
// classpath, so a bare /section.json would resolve to whichever jar comes first.
tasks.processResources {
    from("../section.json") { into("sections/coding-round") }
    from("../prompts") { into("sections/coding-round/prompts") }
}

// The point-and-explain transcript tests run real sessions on a real problem: the Java package at
// test runtime only, and the repository's content folder as input.
dependencies {
    testRuntimeOnly(project(":languages:java:runner"))
}
tasks.test {
    systemProperty("intervue.content", rootProject.file("content").absolutePath)
    inputs.dir(rootProject.file("content/problems/hold-exposure")).withPropertyName("holdExposure")
}
