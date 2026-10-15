package si.intervue.content;

import java.util.List;

/** Outcome of verifying one problem; {@code lines} are printed as-is. */
public record VerifyReport(String problemId, boolean ok, List<String> lines) {}
