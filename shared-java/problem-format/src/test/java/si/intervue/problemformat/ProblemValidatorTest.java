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
    for (String file :
        List.of("brief/intro.md", "brief/phase1.md", "brief/phase2.md", "walkthrough.md")) {
      write(file, "text");
    }
    write("grading/NOTES.md", "notes");
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
}
