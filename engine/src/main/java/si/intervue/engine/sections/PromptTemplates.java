package si.intervue.engine.sections;

/** The section's prompt files; the text is data and never lives in code. */
public interface PromptTemplates {

  /** The template named {@code name} (for example {@code write-code}), with {{placeholders}}. */
  String template(String name);
}
