package si.intervue.engine.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ContentStoreTest {

  private static final Path CONTENT = Path.of(System.getProperty("intervue.content"));
  private static final Path PROBLEMS = CONTENT.resolve("problems");
  private static final Path FREE = CONTENT.resolve("free/free.json");

  @TempDir Path temp;

  private static void copyTree(Path from, Path to) throws IOException {
    try (Stream<Path> walk = Files.walk(from)) {
      for (Path p : walk.toList()) {
        Path target = to.resolve(from.relativize(p).toString());
        if (Files.isDirectory(p)) {
          Files.createDirectories(target);
        } else {
          Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }
  }

  @Test
  void thePlainFolderListsEveryValidProblemInIdOrder() {
    ContentStore store = new PlainFolderSource(PROBLEMS);

    assertThat(store.ids())
        .containsExactly(
            "build-log-bracket-checker",
            "corridor-router",
            "courier-load-balancer",
            "duplicate-order-detector",
            "friend-suggestion",
            "hold-exposure",
            "merchant-suggest",
            "playlist-dedupe",
            "rate-limiter-audit",
            "shift-roster-lookup");
    assertThat(store.load("hold-exposure").problem().title()).isEqualTo("Hold Exposure");
  }

  @Test
  void theFreeSourceOffersOnlyTheProblemsInFreeJson() {
    ContentStore store = new FreeProblemSource(FREE, PROBLEMS);

    assertThat(store.ids()).containsExactly("hold-exposure", "corridor-router");
    assertThatThrownBy(() -> store.load("merchant-suggest")).isInstanceOf(ContentException.class);
  }

  @Test
  void anInvalidProblemIsLeftOutAndExplainsWhyWhenAskedFor() throws IOException {
    copyTree(PROBLEMS.resolve("hold-exposure"), temp.resolve("hold-exposure"));
    copyTree(PROBLEMS.resolve("corridor-router"), temp.resolve("corridor-router"));
    Files.delete(temp.resolve("corridor-router/hints/phase2.md"));
    ContentStore store = new PlainFolderSource(temp);

    assertThat(store.ids()).containsExactly("hold-exposure");
    assertThatThrownBy(() -> store.load("corridor-router"))
        .isInstanceOf(ContentException.class)
        .hasMessageContaining("hints/phase2.md");
  }

  @Test
  void refusesAnIdThatTriesToLeaveTheContentFolder() {
    ContentStore store = new PlainFolderSource(PROBLEMS);

    assertThatThrownBy(() -> store.load("../free")).isInstanceOf(ContentException.class);
    assertThatThrownBy(() -> store.load("no-such-problem")).isInstanceOf(ContentException.class);
  }

  @Test
  void aNullIdIsNotAProblemId() {
    ContentStore store = new PlainFolderSource(PROBLEMS);

    assertThatThrownBy(() -> store.load(null))
        .isInstanceOf(ContentException.class)
        .hasMessageContaining("not a problem id");
  }

  @Test
  void anIdThatIsNotAPathIsNotAProblemId() {
    ContentStore store = new PlainFolderSource(PROBLEMS);

    assertThatThrownBy(() -> store.load("x*"))
        .isInstanceOf(ContentException.class)
        .hasMessageContaining("not a problem id");
  }

  @Test
  void listsBothRoots() throws IOException {
    copyTree(PROBLEMS.resolve("hold-exposure"), temp.resolve("a/hold-exposure"));
    copyTree(PROBLEMS.resolve("corridor-router"), temp.resolve("b/corridor-router"));
    ContentStore store = new PlainFolderSource(List.of(temp.resolve("a"), temp.resolve("b")));

    assertThat(store.ids()).containsExactly("hold-exposure", "corridor-router");
    assertThat(store.load("corridor-router").problem().title()).isEqualTo("Corridor Router");
  }

  @Test
  void duplicateIdAcrossRootsThrows() throws IOException {
    copyTree(PROBLEMS.resolve("hold-exposure"), temp.resolve("a/hold-exposure"));
    copyTree(PROBLEMS.resolve("hold-exposure"), temp.resolve("b/hold-exposure"));

    assertThatThrownBy(() -> new PlainFolderSource(List.of(temp.resolve("a"), temp.resolve("b"))))
        .isInstanceOf(ContentException.class)
        .hasMessageContaining("hold-exposure");
  }
}
