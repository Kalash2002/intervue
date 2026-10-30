// Every Java part of the monorepo (PLAN.md 5.2). Add a line when a part gets its first source file.
rootProject.name = "intervue"

include("engine")
include("shared-java:run-protocol")
include("shared-java:problem-format")
include("languages:java:runner")
include("content:tools")
// Every section package is found by folder, so adding a section edits no build file.
file("sections").listFiles { f -> file("${f.path}/java").isDirectory }.orEmpty().sorted()
    .forEach { include("sections:${it.name}:java") }
