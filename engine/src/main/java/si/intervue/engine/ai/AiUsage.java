package si.intervue.engine.ai;

/** What the route reported for one call; only these fields are ever logged. */
public record AiUsage(
    String route, String model, long inputTokens, long outputTokens, long durationMs) {}
