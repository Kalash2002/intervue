package si.intervue.engine.sections;

import java.util.List;
import si.intervue.problemformat.ProblemFolder;

/** Section-specific checks on top of the general problem format; an empty list means valid. */
public interface ContentSchema {
  List<String> validate(ProblemFolder folder);
}
