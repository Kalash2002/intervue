package si.intervue.engine.languages;

/** Limits for one run: whole-run wall time, child heap, and how much output is kept. */
public record RunLimits(long wallMs, int memoryMb, int outputBytes) {}
