// Every Java part of the monorepo (PLAN.md 5.2). Add a line when a part gets its first source file.
rootProject.name = "intervue"

include("engine")
include("shared-java:run-protocol")
include("shared-java:problem-format")
include("languages:java:runner")
