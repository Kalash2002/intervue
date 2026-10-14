package si.intervue.engine.ai;

/**
 * A whole reply and what it cost; {@code usage} feeds the {@code aiCall} event (never the text).
 */
public record AiReply(String text, AiUsage usage) {}
