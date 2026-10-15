package si.intervue.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CrossLinksTest {

  @TempDir Path root;

  private Path problems() {
    return root.resolve("problems");
  }

  private Path dsa() {
    return root.resolve("dsa");
  }

  private static void write(Path file, String text) throws IOException {
    Files.createDirectories(file.getParent());
    Files.writeString(file, text);
  }

  private void problem(Path rootDir, String id, String coveredBy) throws IOException {
    write(
        rootDir.resolve(id).resolve("problem.json"),
        """
        {"formatVersion":1,"id":"%s","version":1,"coveredBy":[%s]}
        """
            .formatted(id, coveredBy));
  }

  private List<String> check() {
    return CrossLinks.check(problems(), dsa());
  }

  @Test
  void allGood() throws IOException {
    problem(problems(), "hold", "");
    write(problems().resolve("hold/walkthrough.md"), "[Two sum](dsa://two-sum)");
    problem(dsa(), "two-sum", "\"hold\"");
    write(dsa().resolve("INDEX.md"), "plain `dsa://two-sum` and neetcode.io prose");

    assertThat(check()).isEmpty();
  }

  @Test
  void missingDsaTarget() throws IOException {
    problem(problems(), "hold", "");
    write(problems().resolve("hold/walkthrough.md"), "[Two sum](dsa://two-sum)");

    assertThat(check()).singleElement().asString().contains("hold").contains("two-sum");
  }

  @Test
  void missingDsaRootCountsAsEmpty() throws IOException {
    problem(problems(), "hold", "");

    assertThat(check()).isEmpty();
  }

  @Test
  void missingCoveredByTarget() throws IOException {
    problem(dsa(), "two-sum", "\"nope\"");

    assertThat(check()).singleElement().asString().contains("two-sum").contains("nope");
  }

  @Test
  void sameIdInBothRoots() throws IOException {
    problem(problems(), "twin", "");
    problem(dsa(), "twin", "");

    assertThat(check()).singleElement().asString().contains("twin");
  }

  @Test
  void forbiddenSiteLink() throws IOException {
    problem(problems(), "hold", "");
    write(problems().resolve("hold/walkthrough.md"), "[x](https://leetcode.com/problems/a)");

    assertThat(check()).singleElement().asString().contains("hold").contains("leetcode.com");
  }
}
