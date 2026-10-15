package si.intervue.engine.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EngineMainTest {

  @TempDir Path data;

  @Test
  void theShellsTokenIsUsedAndNothingIsWritten() throws Exception {
    assertThat(EngineMain.token("from-shell", data)).isEqualTo("from-shell");
    assertThat(Files.exists(data.resolve("dev-token"))).isFalse();
  }

  @Test
  void withoutAShellTokenOneIsGeneratedAndWrittenForTheDevProxy() throws Exception {
    String token = EngineMain.token(null, data);

    assertThat(token).hasSizeGreaterThanOrEqualTo(32);
    assertThat(Files.readString(data.resolve("dev-token"))).isEqualTo(token);
    assertThat(EngineMain.token("", data)).isNotEqualTo(token);
  }

  @Test
  void argumentsNameTheDataAndContentFolders() {
    EngineMain.Args args =
        EngineMain.Args.parse(new String[] {"--data", "d", "--content", "c", "--port", "4100"});

    assertThat(args.data()).isEqualTo(Path.of("d"));
    assertThat(args.problems()).isEqualTo(Path.of("c", "problems"));
    assertThat(args.port()).isEqualTo(4100);
    assertThat(args.exitOnStdinClose()).isFalse();
    assertThat(
            EngineMain.Args.parse(
                    new String[] {"--exit-on-stdin-close", "--data", "d", "--content", "c"})
                .exitOnStdinClose())
        .isTrue();
    assertThatThrownBy(() -> EngineMain.Args.parse(new String[] {"--data"}))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
