package si.intervue.engine.runners;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Starts a child process, keeps at most a fixed amount of its output, and kills the whole process
 * tree when the wall limit passes. It knows nothing about languages; runners build the command.
 */
public final class ProcessControl {

  /** What a finished or killed child left behind. */
  public record Result(int exitCode, String output, boolean timedOut, boolean outputCut) {}

  public Result run(List<String> command, Path workDir, long wallMs, int outputBytes) {
    Process process;
    try {
      process =
          new ProcessBuilder(command).directory(workDir.toFile()).redirectErrorStream(true).start();
      process.getOutputStream().close();
    } catch (IOException e) {
      throw new UncheckedIOException("Cannot start " + command.get(0), e);
    }
    ByteArrayOutputStream kept = new ByteArrayOutputStream();
    boolean[] cut = new boolean[1];
    Thread reader = new Thread(() -> drain(process.getInputStream(), kept, outputBytes, cut));
    reader.setDaemon(true);
    reader.start();
    boolean finished = waitOrKill(process, wallMs);
    try {
      reader.join(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    synchronized (kept) {
      return new Result(
          finished ? process.exitValue() : -1,
          kept.toString(StandardCharsets.UTF_8),
          !finished,
          cut[0]);
    }
  }

  /** Waits up to {@code wallMs}, then kills the process tree; returns whether it ended in time. */
  public static boolean waitOrKill(Process process, long wallMs) {
    try {
      if (process.waitFor(wallMs, TimeUnit.MILLISECONDS)) {
        return true;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    // Descendants first: killing the parent first would orphan them and hide them from us.
    process.descendants().forEach(ProcessHandle::destroyForcibly);
    process.destroyForcibly();
    return false;
  }

  // Keeps reading past the cap so a chatty child never blocks on a full pipe.
  private static void drain(InputStream in, ByteArrayOutputStream kept, int cap, boolean[] cut) {
    byte[] chunk = new byte[8192];
    try (in) {
      int n;
      while ((n = in.read(chunk)) > 0) {
        synchronized (kept) {
          int room = cap - kept.size();
          kept.write(chunk, 0, Math.max(0, Math.min(n, room)));
          cut[0] |= n > room;
        }
      }
    } catch (IOException e) {
      // The process ended or was killed; what was read is kept.
    }
  }
}
