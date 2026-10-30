package si.intervue.content;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import si.intervue.engine.content.PlainFolderSource;
import si.intervue.engine.languages.LanguagePackage;
import si.intervue.engine.runners.Calibration;
import si.intervue.engine.runners.ProcessControl;

/**
 * Command line for {@link ProblemVerifier}: {@code ./gradlew :content:tools:verify [-Pids=a,b]},
 * run from the repository root. Exit code 0 when every problem checked out.
 */
public final class Verify {

  private Verify() {}

  public static void main(String[] args) throws IOException {
    Path problems = Path.of("content", "problems");
    if (!Files.isDirectory(problems)) {
      System.err.println("Run from the repository root: content/problems was not found.");
      System.exit(2);
    }
    Path dsa = Path.of("content", "dsa");
    List<String> ids = new ArrayList<>(List.of(args));
    if (ids.isEmpty()) {
      for (Path root : List.of(problems, dsa)) {
        ids.addAll(PlainFolderSource.folderNames(root));
      }
    }
    ProcessControl processes = new ProcessControl();
    double factor = javaFactor(processes);
    System.out.printf(Locale.ROOT, "Calibration factor %.2f%n%n", factor);
    Path scratch = Path.of("content", ".build", "verify");
    boolean allOk = true;
    for (String id : ids) {
      VerifyReport report =
          new ProblemVerifier(scratch, factor, processes)
              .verify(root(id, problems, dsa).resolve(id));
      System.out.println("## " + id);
      report.lines().forEach(System.out::println);
      System.out.println((report.ok() ? "OK      " : "FAILED  ") + id + System.lineSeparator());
      allOk &= report.ok();
    }
    ProblemVerifier.deleteTree(scratch);
    // Once per run, after the problems: links are between folders, not owned by one of them.
    List<String> linkErrors = CrossLinks.check(problems, dsa);
    System.out.println("## cross-links");
    linkErrors.forEach(e -> System.out.println("  FAIL  " + e));
    System.out.println(linkErrors.isEmpty() ? "OK      cross-links" : "FAILED  cross-links");
    System.out.println();
    allOk &= linkErrors.isEmpty();
    System.out.println(allOk ? "All problems verified." : "Some problems FAILED verification.");
    System.exit(allOk ? 0 : 1);
  }

  private static Path root(String id, Path problems, Path dsa) {
    return Files.isDirectory(dsa.resolve(id)) ? dsa : problems;
  }

  // Limits in content are reference-machine milliseconds, so verification runs at this machine's
  // factor.
  private static double javaFactor(ProcessControl processes) {
    for (LanguagePackage pkg : ServiceLoader.load(LanguagePackage.class)) {
      if (pkg.definition().id().equals("java")) {
        return Calibration.factor(pkg.benchmark(), processes);
      }
    }
    throw new IllegalStateException("The Java language package is not on the class path");
  }
}
