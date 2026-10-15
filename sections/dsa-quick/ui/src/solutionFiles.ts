import type { FileEntry } from '@intervue/local-engine-client';

/**
 * The files a DSA candidate solves in, in path order: editable, not the main class, not under data/.
 * The engine does not send the variant's `main`; every DSA variant uses `Main` (ponytail: pass it
 * through the file list if a variant ever differs). With no candidate, the first editable file.
 */
export function solutionFiles(files: readonly FileEntry[], main = 'Main'): string[] {
  const editable = files
    .filter((f) => !f.readOnly)
    .map((f) => f.path)
    .sort();
  const picked = editable.filter((p) => p !== `${main}.java` && !p.startsWith('data/'));
  return picked.length ? picked : editable.slice(0, 1);
}
