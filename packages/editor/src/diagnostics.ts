// Compiler diagnostics from the engine shown inline through @codemirror/lint's setDiagnostics.
import { setDiagnostics, type Diagnostic } from '@codemirror/lint';
import type { Text } from '@codemirror/state';
import type { EditorView } from '@codemirror/view';
import type { SourceDiagnostic } from '@intervue/local-engine-client';

const SEVERITY = { error: 'error', warning: 'warning', note: 'info' } as const;

/** Engine positions are 1-based lines and columns; a position past the end clamps to the end. */
export function toDiagnostics(doc: Text, path: string, diagnostics: readonly SourceDiagnostic[]): Diagnostic[] {
  return diagnostics
    .filter((d) => d.path === path)
    .map((d) => {
      if (d.line < 1 || d.line > doc.lines) {
        return { from: doc.length, to: doc.length, severity: SEVERITY[d.severity], message: d.message };
      }
      const line = doc.line(d.line);
      const from = Math.min(line.from + Math.max(0, d.column - 1), line.to);
      const word = /^\w+/.exec(line.text.slice(from - line.from));
      const to = Math.min(from + (word ? word[0].length : 1), line.to);
      return { from, to, severity: SEVERITY[d.severity], message: d.message };
    });
}

export function showDiagnostics(view: EditorView, path: string, diagnostics: readonly SourceDiagnostic[]): void {
  view.dispatch(setDiagnostics(view.state, toDiagnostics(view.state.doc, path, diagnostics)));
}
