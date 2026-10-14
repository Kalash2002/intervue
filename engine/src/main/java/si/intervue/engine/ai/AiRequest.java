package si.intervue.engine.ai;

/**
 * One model call: {@code rules} become the system prompt; {@code prompt} is the assembled user turn
 * (brief, files, history, message) from the PromptBuilder.
 */
public record AiRequest(String rules, String prompt) {}
