// The DSA section package: schema checks, one-phase tests grader, 80/20 scoring rule, prompts.
// Same layout as the coding round, without its real-session transcript tests.
// Every section project is named "java"; without this each jar is lib/java.jar and the next
// section overwrites the last in :engine:engineDist.
base.archivesName = "dsa-quick"

dependencies {
    implementation(project(":engine"))
    implementation(libs.jackson.databind)
}

// section.json and the prompts ship inside the jar under sections/<id>/: every section jar is on one
// classpath, so a bare /section.json would resolve to whichever jar comes first.
tasks.processResources {
    from("../section.json") { into("sections/dsa-quick") }
    from("../prompts") { into("sections/dsa-quick/prompts") }
}
