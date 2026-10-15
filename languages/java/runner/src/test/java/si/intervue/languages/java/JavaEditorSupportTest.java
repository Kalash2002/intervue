package si.intervue.languages.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.languages.SourceSymbol;

class JavaEditorSupportTest {

  @TempDir Path work;
  private final JavaLanguagePackage java = new JavaLanguagePackage();

  @BeforeEach
  void copyFixture() throws IOException {
    try (InputStream in = getClass().getResourceAsStream("/editorwork/HoldBook.java")) {
      Files.copy(in, work.resolve("HoldBook.java"));
    }
  }

  @Test
  void listsTypesMethodsAndFieldsButNotLocals() {
    List<SourceSymbol> symbols = java.editor().symbols(work, List.of("HoldBook.java"));

    assertThat(symbols)
        .extracting(SourceSymbol::name, SourceSymbol::kind, SourceSymbol::container)
        .contains(
            tuple("HoldBook", "class", ""),
            tuple("holds", "field", "HoldBook"),
            tuple("LIMIT", "field", "HoldBook"),
            tuple("add", "method", "HoldBook"),
            tuple("count", "method", "HoldBook"),
            tuple("Entry", "record", "HoldBook"),
            tuple("Listener", "interface", "HoldBook"),
            tuple("changed", "method", "Listener"))
        .noneMatch(t -> t.toList().contains("local"));
    SourceSymbol count =
        symbols.stream().filter(s -> s.name().equals("count")).findFirst().orElseThrow();
    assertThat(count.detail()).isEqualTo("int count(String account, int minute)");
    assertThat(count.path()).isEqualTo("HoldBook.java");
    assertThat(count.line()).isEqualTo(13);
  }

  @Test
  void stillListsSymbolsWhileAFileHasErrors() throws IOException {
    Files.writeString(
        work.resolve("Broken.java"), "class Broken {\n  void ok() {}\n  void bad( {\n}\n");

    assertThat(java.editor().symbols(work, List.of("Broken.java")))
        .extracting(SourceSymbol::name)
        .contains("Broken", "ok");
  }

  @Test
  void reportsCompileErrorsAtTheirLineAndColumnWithoutWritingClasses() throws IOException {
    Files.writeString(
        work.resolve("Uses.java"), "class Uses {\n  int x = new HoldBook().missing();\n}\n");

    var diagnostics = java.editor().diagnostics(work);

    assertThat(diagnostics)
        .anySatisfy(
            d -> {
              assertThat(d.path()).isEqualTo("Uses.java");
              assertThat(d.line()).isEqualTo(2);
              assertThat(d.column()).isPositive();
              assertThat(d.severity()).isEqualTo("error");
            });
    try (Stream<Path> files = Files.walk(work)) {
      assertThat(files.map(Path::toString)).noneMatch(f -> f.endsWith(".class"));
    }
  }

  @Test
  void aCleanProjectHasNoErrors() {
    assertThat(java.editor().diagnostics(work)).noneMatch(d -> d.severity().equals("error"));
  }
}
