import {
  CompletionContext,
  type Completion,
  type CompletionResult,
  type CompletionSource,
} from '@codemirror/autocomplete';
import { EditorState } from '@codemirror/state';
import { EditorView } from '@codemirror/view';
import type { SourceSymbol } from '@intervue/local-engine-client';
import { describe, expect, it, vi } from 'vitest';
import {
  aiEdits,
  changedRange,
  importCompletionSource,
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

describe('importCompletionSource', () => {
  const types = [{ name: 'ArrayList', pkg: 'java.util' }];
  // Accepts ArrayList for a typed "Arr" at the end of `before`, returning the resulting document.
  const accept = (before: string, after = '') => {
    const doc = before + 'Arr' + after;
    const view = new EditorView({ state: EditorState.create({ doc }), parent: document.body });
    const pos = before.length + 3;
    const result = importCompletionSource(types)(new CompletionContext(view.state, pos, false)) as CompletionResult;
    const [opt] = result.options as [Completion];
    (opt.apply as (v: EditorView, c: unknown, f: number, t: number) => void)(view, opt, pos - 3, pos);
    const out = { doc: view.state.doc.toString(), head: view.state.selection.main.head };
    view.destroy();
    return out;
  };

  it('offers the type as a class labelled with its package', () => {
    const result = completeAt('Arr', importCompletionSource(types));

    expect(result?.options).toMatchObject([{ label: 'ArrayList', type: 'class', detail: 'java.util' }]);
  });

  it('adds the import at the top and the word at the cursor, cursor after the word', () => {
    const out = accept('');

    expect(out.doc).toBe('import java.util.ArrayList;\n\nArrayList');
    expect(out.head).toBe(out.doc.length);
  });

  it('adds nothing when java.util.* is imported', () => {
    expect(accept('import java.util.*;\n').doc).toBe('import java.util.*;\nArrayList');
  });

  it('adds nothing when the type is already imported', () => {
    expect(accept('import java.util.ArrayList;\n').doc).toBe('import java.util.ArrayList;\nArrayList');
  });

  it('puts a new import after the last existing import', () => {
    const out = accept('import java.io.File;\nimport java.util.List;\n\nclass A {}\n');

    expect(out.doc).toBe(
      'import java.io.File;\nimport java.util.List;\nimport java.util.ArrayList;\n\nclass A {}\nArrayList',
    );
  });

  it('importGoesAfterThePackageLine', () => {
    const out = accept('package app;\n\nclass A {}\n');

    expect(out.doc).toBe('package app;\n\nimport java.util.ArrayList;\n\nclass A {}\nArrayList');
  });

  it('importGoesAtTheTopWhenThereIsNoPackageLine', () => {
    const out = accept('/** Doc. */\nclass A {}\n');

    expect(out.doc).toBe('import java.util.ArrayList;\n\n/** Doc. */\nclass A {}\nArrayList');
    expect(out.head).toBe(out.doc.length);
  });

  it('yields to a project symbol of the same name, so one Stack entry shows and no import is added', () => {
    const stack = [{ name: 'Stack', pkg: 'java.util' }];
    const doc = 'Sta';
    const state = EditorState.create({ doc });
    const context = new CompletionContext(state, doc.length, false);
    const labels = [
      symbolCompletionSource(() => [symbol('Stack', 'class')]),
      importCompletionSource(stack, () => ['Stack']),
    ].flatMap((source) => (source(context) as CompletionResult).options.map((o) => o.label));

    expect(labels).toEqual(['Stack']);
  });

  it('offers nothing on an unfinished import line', () => {
    expect(completeAt('import java.util.Arr', importCompletionSource(types))).toBeNull();
  });
});
