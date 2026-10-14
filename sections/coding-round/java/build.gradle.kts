// The coding-round section package: schema checks, tests grader, scoring rule, prompts
// (docs/contracts/section-contract.md). One project: grader and scoring ship together and are not
// reused apart, so PLAN.md 5.2's grader/ and scoring/ split would only add build files.
dependencies {
    implementation(project(":engine"))
    implementation(libs.jackson.databind)
}

// section.json and the prompts ship inside the jar.
tasks.processResources {
    from("../section.json")
    from("../prompts") { into("prompts") }
}
