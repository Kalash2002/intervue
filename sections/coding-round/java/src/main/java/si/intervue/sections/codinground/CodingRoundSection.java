package si.intervue.sections.codinground;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import si.intervue.engine.health.HealthCheck;
import si.intervue.engine.sections.AssistantChannel;
import si.intervue.engine.sections.ContentSchema;
import si.intervue.engine.sections.Grader;
import si.intervue.engine.sections.PromptTemplates;
import si.intervue.engine.sections.ResultPart;
import si.intervue.engine.sections.ScoringRule;
import si.intervue.engine.sections.SectionDefinition;
import si.intervue.engine.sections.SectionPackage;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * The coding round (docs/contracts/section-contract.md): three phases graded by tests, both
 * assistant channels, authored hints. Identity comes from {@code section.json} in the jar.
 */
public final class CodingRoundSection implements SectionPackage {

  private static final List<ResultPart> RESULT_PARTS =
      List.of(
          new ResultPart("score", "Score"),
          new ResultPart("phases", "Result by phase"),
          new ResultPart("hints", "Hints used"),
          new ResultPart("edits", "Assistant edits"),
          new ResultPart("walkthrough", "What a strong round looks like"),
          new ResultPart("follow-ups", "Follow-up questions"),
          new ResultPart("notes", "Your notes"));

  private final SectionDefinition definition = readDefinition();
  private final TestsGrader grader = new TestsGrader();
  private final CodingRoundScore scoring = new CodingRoundScore();

  private static SectionDefinition readDefinition() {
    try (InputStream in = CodingRoundSection.class.getResourceAsStream("/section.json")) {
      if (in == null) {
        throw new IllegalStateException("section.json is missing from the coding-round jar");
      }
      return JsonMapper.builder()
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .build()
          .readValue(in, SectionDefinition.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public SectionDefinition definition() {
    return definition;
  }

  /** The general format already requires hints for every phase; this adds the section match. */
  @Override
  public ContentSchema schema() {
    return folder -> {
      List<String> errors = new ArrayList<>();
      if (!definition.id().equals(folder.problem().section())) {
        errors.add("section must be " + definition.id() + " for this package");
      }
      return errors;
    };
  }

  @Override
  public Grader grader() {
    return grader;
  }

  @Override
  public ScoringRule scoring() {
    return scoring;
  }

  @Override
  public List<ResultPart> resultParts() {
    return RESULT_PARTS;
  }

  // Hints are already required by the schema; nothing else to probe in Phase 1a.
  @Override
  public List<HealthCheck> healthChecks() {
    return List.of();
  }

  @Override
  public PromptTemplates prompts() {
    return name -> {
      try (InputStream in =
          CodingRoundSection.class.getResourceAsStream("/prompts/" + name + ".md")) {
        if (in == null) {
          throw new IllegalArgumentException("No prompt template named " + name);
        }
        return new String(in.readAllBytes(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    };
  }

  @Override
  public Set<AssistantChannel> channels() {
    return EnumSet.of(AssistantChannel.ASK, AssistantChannel.WRITE_CODE);
  }

  @Override
  public boolean supportsHints() {
    return true;
  }
}
