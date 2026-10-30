package si.intervue.problemformat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Each test starts from one valid problem and breaks exactly one rule of the contract. */
class ProblemValidatorTest {

  private static final String HINT =
      "## Level 1\nLook at the parser.\n\n## Level 2\nSort by start.\n\n## Level 3\nSweep the"
          + " events.\n";

  private static final String WALKTHROUGH =
      "# How a strong round goes\n\n## Phase 1\n\ntext\n\n## DSA reference\n\n### Theory\n\n"
          + "![A stack](figures/stack.svg)\n\n### Code\n\ncode\n\n### Complexity\n\ntable\n\n"
          + "### Practise the technique\n\n- [Two Sum](dsa://two-sum)\n\n## Time budget\n\ntable\n\n"
          + "## Follow-up answers\n\ntext\n";

  @TempDir Path root;
  private Path dir;

  @BeforeEach
  void writeValidProblem() throws IOException {
    dir = root.resolve("hold-exposure");
    write(
        "problem.json",
        """
        {"formatVersion":1,"id":"hold-exposure","version":3,"section":"coding-round",
         "title":"Hold Exposure","summary":"Totals holds.","difficulty":"Easy",
         "techniques":["intervals"],"domain":"fintech","minutes":60,
         "phases":[
          {"id":1,"title":"Fix","brief":"brief/phase1.md","hints":"hints/phase1.md","weight":20,"allOrNothing":true,"timeoutRefMs":30000},
          {"id":2,"title":"Peak","brief":"brief/phase2.md","hints":"hints/phase2.md","weight":50,"allOrNothing":false,"timeoutRefMs":30000}],
         "followUps":["Why?"],"languages":["java"]}
        """);
    write(
        "variants/java/variant.json",
        """
        {"language":"java","minToolchain":"21","main":"Main",
         "readOnly":["Phase1Tests.java","data/holds.csv"],
         "tests":{"1":"Phase1Tests","2":"Phase2Tests"}}
        """);
    write("variants/java/starter/Phase1Tests.java", "class Phase1Tests {}");
    write("variants/java/starter/data/holds.csv", "a,b");
    for (String file : List.of("brief/intro.md", "brief/phase1.md", "brief/phase2.md")) {
      write(file, "text");
    }
    write("walkthrough.md", WALKTHROUGH);
    write("grading/NOTES.md", "notes");
    write("figures/stack.svg", "<svg xmlns='http://www.w3.org/2000/svg'/>");
    write("hints/phase1.md", HINT);
    write("hints/phase2.md", HINT);
  }

  private void write(String relative, String content) throws IOException {
    Path file = dir.resolve(relative);
    Files.createDirectories(file.getParent());
    Files.writeString(file, content, StandardCharsets.UTF_8);
  }

  private void replaceInProblem(String from, String to) throws IOException {
    Path file = dir.resolve("problem.json");
    String json = Files.readString(file);
    assertThat(json).contains(from);
    Files.writeString(file, json.replace(from, to));
  }

  private List<String> errors() {
    return ProblemValidator.validate(ProblemReader.read(dir));
  }

  @Test
  void acceptsAValidProblem() {
    assertThat(errors()).isEmpty();
  }

  @Test
  void readsTheProblemAndItsVariant() {
    ProblemFolder folder = ProblemReader.read(dir);

    assertThat(folder.problem().title()).isEqualTo("Hold Exposure");
    assertThat(folder.problem().phases()).extracting(Phase::weight).containsExactly(20, 50);
    assertThat(folder.variants().get("java").tests()).containsEntry("2", "Phase2Tests");
  }

  @Test
  void tagsAreOptionalAndDefaultToEmpty() {
    assertThat(ProblemReader.read(dir).problem().tags()).isEmpty();
    assertThat(errors()).isEmpty();
  }

  @Test
  void acceptsLowerCaseTags() throws IOException {
    replaceInProblem(
        "\"domain\":\"fintech\"", "\"domain\":\"fintech\",\"tags\":[\"intervals\",\"fintech\"]");
    assertThat(ProblemReader.read(dir).problem().tags()).containsExactly("intervals", "fintech");
    assertThat(errors()).isEmpty();
  }

  @Test
  void refusesTagsThatAreNotLowerCaseWithDashes() throws IOException {
    replaceInProblem(
        "\"domain\":\"fintech\"",
        "\"domain\":\"fintech\",\"tags\":[\"Graphs\",\"two pointers\",null]");
    assertThat(errors()).anyMatch(e -> e.contains("tags"));
  }

  @Test
  void refusesDuplicateTags() throws IOException {
    replaceInProblem(
        "\"domain\":\"fintech\"", "\"domain\":\"fintech\",\"tags\":[\"graphs\",\"graphs\"]");
    assertThat(errors()).anyMatch(e -> e.contains("tags"));
  }

  @Test
  void refusesAWalkthroughWithoutTheDsaReference() throws IOException {
    write("walkthrough.md", WALKTHROUGH.replace("## DSA reference\n", "## Technique\n"));
    assertThat(errors()).anyMatch(e -> e.contains("DSA reference"));
  }

  @Test
  void refusesWalkthroughHeadingsOutOfOrder() throws IOException {
    write(
        "walkthrough.md",
        WALKTHROUGH.replace("## Time budget\n\ntable\n\n", "") + "\n## Time budget\n\ntable\n");
    assertThat(errors()).anyMatch(e -> e.contains("Follow-up answers"));
  }

  @Test
  void refusesAFigureThatDoesNotExist() throws IOException {
    write("walkthrough.md", WALKTHROUGH.replace("figures/stack.svg", "figures/heap.svg"));
    assertThat(errors()).anyMatch(e -> e.contains("figures/heap.svg"));
  }

  @Test
  void refusesAnIdThatDiffersFromTheFolder() throws IOException {
    replaceInProblem("\"id\":\"hold-exposure\"", "\"id\":\"other\"");
    assertThat(errors()).anyMatch(e -> e.contains("id"));
  }

  @Test
  void refusesAnUnknownFormatVersion() throws IOException {
    replaceInProblem("\"formatVersion\":1", "\"formatVersion\":2");
    assertThat(errors()).anyMatch(e -> e.contains("formatVersion"));
  }

  @Test
  void refusesWeightsThatDoNotSumToSeventy() throws IOException {
    replaceInProblem("\"weight\":50", "\"weight\":40");
    assertThat(errors()).anyMatch(e -> e.contains("70"));
  }

  @Test
  void refusesPhasesOutOfOrder() throws IOException {
    replaceInProblem("{\"id\":2,", "{\"id\":3,");
    assertThat(errors()).anyMatch(e -> e.contains("phase ids"));
  }

  @Test
  void refusesAnUnknownDifficulty() throws IOException {
    replaceInProblem("\"Easy\"", "\"Trivial\"");
    assertThat(errors()).anyMatch(e -> e.contains("difficulty"));
  }

  @Test
  void refusesAMissingBrief() throws IOException {
    Files.delete(dir.resolve("brief/phase2.md"));
    assertThat(errors()).anyMatch(e -> e.contains("brief/phase2.md"));
  }

  @Test
  void refusesAPhaseWithoutHints() throws IOException {
    Files.delete(dir.resolve("hints/phase2.md"));
    assertThat(errors()).anyMatch(e -> e.contains("hints/phase2.md"));
  }

  @Test
  void refusesHintsMissingALevel() throws IOException {
    write("hints/phase1.md", "## Level 1\nLook.\n\n## Level 2\nSort.\n");
    assertThat(errors()).anyMatch(e -> e.contains("Level 3"));
  }

  @Test
  void refusesAnEmptyHintLevel() throws IOException {
    write("hints/phase1.md", "## Level 1\nLook.\n\n## Level 2\n\n## Level 3\nSweep.\n");
    assertThat(errors()).anyMatch(e -> e.contains("Level 2"));
  }

  @Test
  void refusesCodeInHints() throws IOException {
    write("hints/phase1.md", HINT + "\n```java\nint x;\n```\n");
    assertThat(errors()).anyMatch(e -> e.contains("code"));
  }

  @Test
  void refusesAReadOnlyFileMissingFromTheStarter() throws IOException {
    Files.delete(dir.resolve("variants/java/starter/data/holds.csv"));
    assertThat(errors()).anyMatch(e -> e.contains("data/holds.csv"));
  }

  @Test
  void refusesAPhaseWithoutATestClass() throws IOException {
    write(
        "variants/java/variant.json",
        """
        {"language":"java","minToolchain":"21","main":"Main",
         "readOnly":["Phase1Tests.java"],"tests":{"1":"Phase1Tests"}}
        """);
    assertThat(errors()).anyMatch(e -> e.contains("phase 2"));
  }

  @Test
  void refusesADeclaredLanguageWithoutAVariant() throws IOException {
    replaceInProblem("\"languages\":[\"java\"]", "\"languages\":[\"java\",\"python\"]");
    assertThat(errors()).anyMatch(e -> e.contains("python"));
  }

  @Test
  void refusesAProblemWithoutALanguage() throws IOException {
    replaceInProblem("\"languages\":[\"java\"]", "\"languages\":[]");
    assertThat(errors()).anyMatch(e -> e.contains("at least one language"));
  }

  @Test
  void refusesAMissingWalkthroughOrGradingNotes() throws IOException {
    Files.delete(dir.resolve("walkthrough.md"));
    Files.delete(dir.resolve("grading/NOTES.md"));
    assertThat(errors())
        .anyMatch(e -> e.contains("walkthrough.md"))
        .anyMatch(e -> e.contains("grading/NOTES.md"));
  }

  @Test
  void reportsUnreadableJsonAsAFormatError() throws IOException {
    write("problem.json", "{ not json");
    assertThatThrownBy(() -> ProblemReader.read(dir))
        .isInstanceOf(ProblemFormatException.class)
        .hasMessageContaining("problem.json");
  }

  private static final String DSA_ANSWER =
      """
      ## The question

      text

      ## How to think about it

      text

      ![A row of cells](figures/stack.svg)

      ## Brute force

      ```java
      int a = 1;
      ```

      **Complexity:** time O(n^2), extra space O(1).

      ## Better: Sort first

      ```java
      int b = 1;
      ```

      **Complexity:** time O(n log n), extra space O(1).

      ## Optimal: One pass

      ```java
      int c = 1;
      ```

      **Complexity:** time O(n), extra space O(n).

      ## Complexity

      table

      ## Where it shows up

      text

      ## Follow-up answers

      text
      """;

  private static final String DSA_STATEMENT =
      """
      # Hold Exposure

      Total the holds.

      ## Example 1

      **Input:** `[1, 2]`
      **Output:** `3`
      **Explanation:** One plus two.

      ## Example 2

      **Input:** `[]`
      **Output:** `0`
      **Explanation:** Nothing to add.

      ## Constraints

      - At most 1000 holds.

      ## Follow-up

      Can you do it in one pass?
      """;

  /** Turns the fixture into a dsa-quick problem: one phase, a statement, no intro. */
  private void makeDsa(String section, int weight) throws IOException {
    write(
        "problem.json",
        """
        {"formatVersion":1,"id":"hold-exposure","version":3,"section":"%s",
         "title":"Hold Exposure","summary":"Totals holds.","difficulty":"Easy",
         "techniques":["intervals"],"domain":"fintech","minutes":20,
         "phases":[
          {"id":1,"title":"Solve","brief":"brief/statement.md","hints":"hints/phase1.md","weight":%d,"allOrNothing":false,"timeoutRefMs":30000}],
         "followUps":["Why?"],"languages":["java"]}
        """
            .formatted(section, weight));
    write(
        "variants/java/variant.json",
        "{\"language\":\"java\",\"minToolchain\":\"21\","
            + "\"main\":\"Main\",\"readOnly\":[\"Phase1Tests.java\"],\"tests\":{\"1\":\"Phase1Tests\"}}");
    write("variants/java/starter/Main.java", "class Main {}");
    write("variants/java/starter/Calc.java", "import java.util.*;\nclass Calc {}");
    write("brief/statement.md", DSA_STATEMENT);
    write("walkthrough.md", DSA_ANSWER);
    Files.delete(dir.resolve("brief/intro.md"));
  }

  private void makeDsa(int weight) throws IOException {
    makeDsa(ProblemValidator.DSA_SECTION, weight);
  }

  @Test
  void dsaStatementInShapePasses() throws IOException {
    makeDsa(80);
    assertThat(errors()).isEmpty();
  }

  @Test
  void dsaWeightOtherThan80IsRefused() throws IOException {
    makeDsa(70);
    assertThat(errors()).anyMatch(e -> e.contains("must be 80"));
  }

  @Test
  void onePhaseProblemOfAnotherSectionGetsTheMultiPhaseRules() throws IOException {
    makeDsa("coding-round", 80);
    assertThat(errors())
        .anyMatch(e -> e.contains("must be 70"))
        .anyMatch(e -> e.contains("brief/intro.md"))
        .anyMatch(e -> e.contains("## DSA reference"));
  }

  @Test
  void dsaStatementWithoutConstraintsIsRefused() throws IOException {
    makeDsa(80);
    write("brief/statement.md", DSA_STATEMENT.replace("## Constraints", "## Limits"));
    assertThat(errors()).anyMatch(e -> e.contains("## Constraints"));
  }

  @Test
  void dsaExampleWithoutOutputIsRefused() throws IOException {
    makeDsa(80);
    write("brief/statement.md", DSA_STATEMENT.replace("**Output:** `0`", "**Output:**"));
    assertThat(errors()).anyMatch(e -> e.contains("Example 2 has no \"**Output:**\" text"));
  }

  @Test
  void dsaAnswerNeedsBruteBetterOptimalInOrder() throws IOException {
    makeDsa(80);
    write("walkthrough.md", DSA_ANSWER.replace("## Better: Sort first", "## Better: "));
    assertThat(errors()).anyMatch(e -> e.contains("## Better: <name>"));
    write("walkthrough.md", DSA_ANSWER.replace("## Optimal: One pass", "## Optimal: "));
    assertThat(errors()).anyMatch(e -> e.contains("## Optimal: <name>"));
  }

  @Test
  void dsaApproachNeedsCodeAndComplexityLine() throws IOException {
    makeDsa(80);
    write("walkthrough.md", DSA_ANSWER.replace("```java", "```text"));
    assertThat(errors()).anyMatch(e -> e.contains("Brute force") && e.contains("```java"));
    write("walkthrough.md", DSA_ANSWER.replace("**Complexity:** time O(n), ", "time O(n), "));
    assertThat(errors()).anyMatch(e -> e.contains("Optimal: One pass") && e.contains("Complexity"));
  }

  @Test
  void dsaAnswerWithoutFigureIsRefused() throws IOException {
    makeDsa(80);
    write("walkthrough.md", DSA_ANSWER.replace("![A row of cells](figures/stack.svg)", "text"));
    assertThat(errors()).anyMatch(e -> e.contains("at least one figure"));
  }

  @Test
  void dsaStarterWithoutJavaUtilImportIsRefused() throws IOException {
    makeDsa(80);
    write("variants/java/starter/Calc.java", "class Calc {}");
    assertThat(errors()).anyMatch(e -> e.contains("Calc.java") && e.contains("java.util"));
  }

  @Test
  void dsaAnswerWithOptimalBeforeBetterIsRefused() throws IOException {
    makeDsa(80);
    write(
        "walkthrough.md",
        DSA_ANSWER
            .replace("## Better: Sort first", "## Optimal: X")
            .replace("## Optimal: One pass", "## Better: Y"));
    assertThat(errors()).anyMatch(e -> e.contains("## Optimal: <name>"));
  }

  @Test
  void dsaAnswerWithBruteForceAfterBetterIsRefused() throws IOException {
    makeDsa(80);
    write(
        "walkthrough.md",
        DSA_ANSWER
            .replace("## Brute force", "## Better: A")
            .replace("## Better: Sort first", "## Brute force"));
    assertThat(errors()).anyMatch(e -> e.contains("## Better: <name>"));
  }

  @Test
  void dsaAnswerWithTwoBettersInOrderPasses() throws IOException {
    makeDsa(80);
    String second =
        "## Better: Use a map\n\n```java\nint d = 1;\n```\n\n**Complexity:** time O(n), extra"
            + " space O(n).\n\n## Optimal: One pass";
    write("walkthrough.md", DSA_ANSWER.replace("## Optimal: One pass", second));
    assertThat(errors()).isEmpty();
  }

  @Test
  void dsaStatementWithSkippedExampleNumberIsRefused() throws IOException {
    makeDsa(80);
    write("brief/statement.md", DSA_STATEMENT.replace("## Example 2", "## Example 3"));
    assertThat(errors()).anyMatch(e -> e.contains("## Constraints") && e.contains("Example 3"));
  }

  @Test
  void dsaStatementWithoutTitleIsRefused() throws IOException {
    makeDsa(80);
    write("brief/statement.md", DSA_STATEMENT.replace("# Hold Exposure", "Hold Exposure"));
    assertThat(errors()).anyMatch(e -> e.contains("title line"));
  }

  @Test
  void dsaStatementWithStrayHeadingBeforeConstraintsIsRefused() throws IOException {
    makeDsa(80);
    write(
        "brief/statement.md",
        DSA_STATEMENT.replace("## Constraints", "## Notes\n\ntext\n\n## Constraints"));
    assertThat(errors()).anyMatch(e -> e.contains("## Constraints") && e.contains("## Notes"));
  }

  @Test
  void dsaStatementFollowUpIsOptional() throws IOException {
    makeDsa(80);
    write("brief/statement.md", DSA_STATEMENT.substring(0, DSA_STATEMENT.indexOf("## Follow-up")));
    assertThat(errors()).isEmpty();
  }

  @Test
  void dsaStatementWithHeadingAfterConstraintsOrFollowUpIsRefused() throws IOException {
    makeDsa(80);
    String noFollowUp = DSA_STATEMENT.substring(0, DSA_STATEMENT.indexOf("## Follow-up"));
    write("brief/statement.md", noFollowUp + "## Notes\n\ntext\n");
    assertThat(errors()).anyMatch(e -> e.contains("unexpected heading") && e.contains("## Notes"));
    write("brief/statement.md", DSA_STATEMENT + "\n## Notes\n\ntext\n");
    assertThat(errors()).anyMatch(e -> e.contains("unexpected heading") && e.contains("## Notes"));
  }

  @Test
  void dsaStarterImportRuleSkipsMainAndReadOnlyFilesButNamesOtherEditableOnes() throws IOException {
    makeDsa(80);
    // Main.java (no import) and the read-only test file (no import) are exempt.
    write("variants/java/starter/Phase1Tests.java", "class Phase1Tests {}");
    assertThat(errors()).isEmpty();
    write("variants/java/starter/Extra.java", "class Extra {}");
    assertThat(errors()).hasSize(1).allMatch(e -> e.contains("Extra.java"));
  }

  @Test
  void codingRoundStillUsesItsHeadings() throws IOException {
    assertThat(errors()).isEmpty();
    write("walkthrough.md", WALKTHROUGH.replace("## DSA reference\n", "## Technique\n"));
    assertThat(errors()).anyMatch(e -> e.contains("## DSA reference"));
  }

  @Test
  void multiPhaseStillNeedsIntroAndSeventy() throws IOException {
    Files.delete(dir.resolve("brief/intro.md"));
    assertThat(errors()).anyMatch(e -> e.contains("brief/intro.md"));
  }

  @Test
  void coveredByDefaultsToEmpty() {
    assertThat(ProblemReader.read(dir).problem().coveredBy()).isEqualTo(List.of());
  }

  @Test
  void refusesBadCoveredByIds() throws IOException {
    replaceInProblem(
        "\"domain\":\"fintech\"", "\"domain\":\"fintech\",\"coveredBy\":[\"Hold Exposure\"]");
    assertThat(errors()).anyMatch(e -> e.contains("coveredBy must be problem ids"));
  }
}
