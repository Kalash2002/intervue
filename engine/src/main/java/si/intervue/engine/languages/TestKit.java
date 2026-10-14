package si.intervue.engine.languages;

import java.nio.file.Path;
import java.util.List;

/** The language's test kit; installed into every work folder and restored before every run. */
public interface TestKit {

  /** Writes the kit files into {@code workDir}, replacing any changed copy. */
  void install(Path workDir);

  /** Paths of the kit files relative to the work folder; never editable. */
  List<String> files();
}
