package si.intervue.engine.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClaudeCliTest {

  @TempDir Path dir;

  private Path file(String relative) throws IOException {
    Path p = dir.resolve(relative);
    Files.createDirectories(p.getParent());
    return Files.writeString(p, "x");
  }

  @Test
  void anOverrideWinsOverPath() throws IOException {
    Path exe = file("custom/claude.exe");
    file("bin/claude.exe");

    assertThat(ClaudeCli.locate(exe.toString(), dir.resolve("bin").toString(), true)).contains(exe);
  }

  @Test
  void findsTheNativeExecutableOnPath() throws IOException {
    Path exe = file("bin/claude.exe");

    assertThat(ClaudeCli.locate(null, "nowhere" + File.pathSeparator + dir.resolve("bin"), true))
        .contains(exe);
  }

  @Test
  void looksThroughTheNpmShimToItsNativeExecutable() throws IOException {
    file("npm/claude.cmd");
    Path exe = file("npm/node_modules/@anthropic-ai/claude-code/bin/claude.exe");

    // cmd.exe would re-parse every argument; the native executable takes them as they are.
    assertThat(ClaudeCli.locate(null, dir.resolve("npm").toString(), true)).contains(exe);
  }

  @Test
  void nothingFoundIsEmpty() {
    assertThat(ClaudeCli.locate(null, dir.toString(), true)).isEqualTo(Optional.empty());
  }

  @Test
  void signedInWhenVersionAndAuthStatusSucceed() {
    AiStatus status = new ClaudeCli(FakeCli.command("ok", dir)).probe();

    assertThat(status.available()).isTrue();
    assertThat(status.reason()).isNull();
  }

  @Test
  void signedOutIsReportedWithTheFix() {
    AiStatus status = new ClaudeCli(FakeCli.command("signed-out", dir)).probe();

    assertThat(status.available()).isFalse();
    assertThat(status.reason()).contains("signed out").contains("claude");
  }

  @Test
  void aBrokenCliIsReportedByExitCodeNotByItsOutput() {
    AiStatus status = new ClaudeCli(FakeCli.command("broken", dir)).probe();

    assertThat(status.available()).isFalse();
    assertThat(status.reason()).contains("exit code 3").doesNotContain("something went wrong");
  }

  @Test
  void aMissingCliIsNotFound() {
    AiStatus status = new ClaudeCli(List.of(dir.resolve("no-such-claude.exe").toString())).probe();

    assertThat(status.available()).isFalse();
    assertThat(status.reason()).contains("not found");
  }
}
