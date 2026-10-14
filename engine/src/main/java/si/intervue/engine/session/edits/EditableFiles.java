package si.intervue.engine.session.edits;

import java.nio.file.Path;
import java.util.List;

/**
 * The session's file rules the applier must obey. Implemented by the session's work folder; an
 * interface only so this package does not depend on the session package that uses it.
 */
public interface EditableFiles {

  Path root();

  /** The file for a user- or model-supplied path; throws when the path could leave the folder. */
  Path resolve(String path);

  /** False for read-only, kit and data files. */
  boolean isEditable(String path);

  /** Extensions of source files the language treats as code, such as ".java". */
  List<String> sourceExtensions();
}
