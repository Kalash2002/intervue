package si.intervue.runprotocol;

/** The runner's closing line; absent when the runner was killed before writing it. */
public record Summary(int passed, int failed, int timeouts, boolean compileError, long wallMs) {}
