// The coding-round section package: schema checks, tests grader, scoring rule, prompts
// (docs/contracts/section-contract.md). One project: grader and scoring ship together and are not
// reused apart, so PLAN.md 5.2's grader/ and scoring/ split would only add build files.
dependencies {
    implementation(project(":engine"))
    implementation(libs.jackson.databind)
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
