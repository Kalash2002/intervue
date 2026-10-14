package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TimelineReportTest {

  private static StoredEvent ai(int seq, long tMs, String kind) {
    return new StoredEvent(seq, tMs, "aiCall", Map.of("kind", kind, "ok", true));
  }

  @Test
  void countsTheCandidatesAssistantCallsAsPromptsButNotTheReview() {
    List<StoredEvent> events =
        List.of(
            ai(1, 1_000, "chat"),
            ai(2, 2_000, "edit"),
            ai(3, 3_000, "hint"),
            new StoredEvent(4, 4_000, "run", Map.of("compileError", false)),
            ai(5, 5_000, "chat"),
            ai(6, 9_000, "feedback"));

    TimelineReport r = TimelineReport.of(events, 10_000);

    assertThat(r.prompts()).isEqualTo(4);
    assertThat(r.firstPromptMs()).isEqualTo(1_000);
    assertThat(r.longestPromptStreak()).isEqualTo(3);
  }
}
