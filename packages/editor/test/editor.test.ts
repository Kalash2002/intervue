import { CompletionContext, type CompletionResult, type CompletionSource } from '@codemirror/autocomplete';
import { EditorState } from '@codemirror/state';
import { EditorView } from '@codemirror/view';
import type { SourceSymbol } from '@intervue/local-engine-client';
import { describe, expect, it, vi } from 'vitest';
import {
  aiEdits,
  changedRange,
  lockedRanges,
  markAiEdit,
  setLockedRanges,
  symbolCompletionSource,
  toDiagnostics,
  wordCompletionSource,
} from '../src/index.ts';

const symbol = (name: string, kind: string, detail = name): SourceSymbol => ({
  name,
  kind,
  container: 'HoldBook',
  detail,
  path: 'HoldBook.java',
  line: 1,
  column: 1,
});

const completeAt = (doc: string, source: CompletionSource) => {
  const state = EditorState.create({ doc });
  // Both sources answer synchronously.
  return source(new CompletionContext(state, doc.length, false)) as CompletionResult | null;
};

describe('completion sources', () => {
  it('offers project symbols that match the word being typed', () => {
    const source = symbolCompletionSource(() => [
      symbol('totalHeld', 'method', 'long totalHeld(String account)'),
      symbol('HoldBook', 'class'),
    ]);

    const result = completeAt('book.tot', source);

    expect(result?.from).toBe(5);
    expect(result?.options.map((o) => o.label)).toEqual(['totalHeld', 'HoldBook']);
    expect(result?.options[0]).toMatchObject({ type: 'method', detail: 'long totalHeld(String account)' });
  });

  it('stays quiet before anything is typed unless asked', () => {
    expect(
      completeAt(
        'book.',
        symbolCompletionSource(() => [symbol('x', 'field')]),
      ),
    ).toBeNull();
  });

  it('offers keywords and snippets from the language package', () => {
    const source = wordCompletionSource([{ label: 'public', type: 'keyword' }]);

    expect(completeAt('pub', source)?.options[0].label).toBe('public');
  });
});

describe('diagnostics', () => {
  it('maps engine line and column to document offsets for the open file only', () => {
    const doc = EditorState.create({ doc: 'class A {\n  int x = "s";\n}\n' }).doc;

    const diagnostics = toDiagnostics(doc, 'A.java', [
      { path: 'A.java', line: 2, column: 11, severity: 'error', message: 'incompatible types' },
      { path: 'B.java', line: 1, column: 1, severity: 'error', message: 'elsewhere' },
      { path: 'A.java', line: 9, column: 1, severity: 'note', message: 'past the end' },
    ]);

    expect(diagnostics).toHaveLength(2);
    expect(diagnostics[0]).toMatchObject({ from: 20, to: 21, severity: 'error', message: 'incompatible types' });
    expect(diagnostics[1]).toMatchObject({ from: doc.length, to: doc.length, severity: 'info' });
  });
});

describe('locked ranges', () => {
  const state = () => {
    const base = EditorState.create({ doc: '0123456789', extensions: lockedRanges });
    return base.update({ effects: setLockedRanges.of([{ from: 2, to: 5 }]) }).state;
  };

  it('refuses typing inside a locked range', () => {
    expect(
      state()
        .update({ changes: { from: 3, insert: 'x' } })
        .state.doc.toString(),
    ).toBe('0123456789');
  });

  it('allows typing outside it', () => {
    expect(
      state()
        .update({ changes: { from: 8, insert: 'x' } })
        .state.doc.toString(),
    ).toBe('01234567x89');
  });

  it('lets the engine write through it with filter false', () => {
    const edited = state().update({ changes: { from: 3, insert: 'x' }, filter: false });
    expect(edited.state.doc.toString()).toBe('012x3456789');
  });
});

describe('AI edits', () => {
  it('finds the changed lines between two versions', () => {
    expect(changedRange('a\nb\nc\n', 'a\nB\nB2\nc\n')).toEqual({ fromLine: 2, toLine: 3 });
    expect(changedRange('same', 'same')).toBeNull();
  });

  it('marks the edit and its Undo button calls back with the edit id', () => {
    const onUndo = vi.fn();
    const view = new EditorView({
      state: EditorState.create({ doc: 'a\nb\nc\n', extensions: aiEdits(onUndo) }),
      parent: document.body,
    });

    markAiEdit(view, { editId: 'r1:e1', fromLine: 2, toLine: 2 });
    const button = view.dom.querySelector<HTMLButtonElement>('.cm-ai-undo');
    button?.click();

    expect(view.dom.querySelector('.cm-ai-edit')).not.toBeNull();
    expect(onUndo).toHaveBeenCalledWith('r1:e1');
    view.destroy();
  });
});
