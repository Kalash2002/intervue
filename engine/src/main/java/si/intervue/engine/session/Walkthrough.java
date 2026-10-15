package si.intervue.engine.session;

import java.util.Map;

/**
 * A problem's walkthrough for the Answer page: the Markdown and its figures inlined as data URIs,
 * keyed by the relative path the Markdown uses ({@code figures/<name>}), because an {@code <img>}
 * cannot carry the API token.
 */
public record Walkthrough(String text, Map<String, String> figures) {}
