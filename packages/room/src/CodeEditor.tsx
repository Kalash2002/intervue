// React wrapper around one CodeMirror view. The view is created once per file (the parent keys it by
// path); later prop changes are pushed into it as transactions, never by recreating it.
import { useEffect, useRef } from 'react';
import { EditorState } from '@codemirror/state';
import { EditorView } from '@codemirror/view';
import {
  clearAiEdits,
  editorExtensions,
  markAiEdit,
  showDiagnostics,
  symbolCompletionSource,
  wordCompletionSource,
  importCompletionSource,
  type AiEditMark,
  type LanguageEditor,
} from '@intervue/editor';
import type { SourceDiagnostic, SourceSymbol } from '@intervue/local-engine-client';

export interface CodeEditorProps {
  path: string;
  text: string;
  readOnly: boolean;
  language: LanguageEditor | undefined;
  symbols: readonly SourceSymbol[];
  diagnostics: readonly SourceDiagnostic[];
  marks: readonly AiEditMark[];
  onChange: (text: string) => void;
  onUndo: (editId: string) => void;
  onSave: () => void;
  onRun: () => void;
}

/** Project symbols, the language's words, and its importable library types. */
function completionSourcesFor(language: LanguageEditor | undefined, symbols: () => readonly SourceSymbol[]) {
  return [
    symbolCompletionSource(symbols),
    wordCompletionSource(language?.words ?? []),
    ...(language?.importableTypes ? [importCompletionSource(language.importableTypes)] : []),
  ];
}

export function CodeEditor(props: CodeEditorProps) {
  const host = useRef<HTMLDivElement>(null);
  const view = useRef<EditorView | null>(null);
  const latest = useRef(props);
  useEffect(() => {
    latest.current = props;
  });

  const { readOnly, language, text, path, diagnostics, marks } = props;
  useEffect(() => {
    const call = (fn: () => void) => () => (fn(), true);
    const created = new EditorView({
      parent: host.current ?? undefined,
      state: EditorState.create({
        doc: latest.current.text,
        extensions: editorExtensions({
          language: language?.language() ?? [],
          completionSources: completionSourcesFor(language, () => latest.current.symbols),
          readOnly,
          onChange: (doc) => latest.current.onChange(doc),
          onUndoAiEdit: (id) => latest.current.onUndo(id),
          keys: [
            { key: 'Mod-s', run: call(() => latest.current.onSave()), preventDefault: true },
            { key: 'Mod-Enter', run: call(() => latest.current.onRun()), preventDefault: true },
          ],
        }),
      }),
    });
    view.current = created;
    return () => created.destroy();
  }, [readOnly, language]);

  // Engine-side changes (reset, undo, AI edits) bypass the locked-range filter like any engine write.
  // Replacing the whole document drops its decorations, so the AI-edit marks are laid again.
  useEffect(() => {
    const v = view.current;
    if (v && v.state.doc.toString() !== text) {
      v.dispatch({ changes: { from: 0, to: v.state.doc.length, insert: text }, filter: false });
      applyMarks(v, latest.current.marks);
    }
  }, [text]);

  useEffect(() => {
    if (view.current) showDiagnostics(view.current, path, diagnostics);
  }, [path, diagnostics]);

  // Typing maps existing marks through the change; only a new set of marks needs a redraw.
  useEffect(() => {
    if (view.current) applyMarks(view.current, marks);
  }, [marks]);

  return (
    <div className="editor-wrap" ref={host}>
      {readOnly && <div className="readonly-note">read-only</div>}
    </div>
  );
}

function applyMarks(view: EditorView, marks: readonly AiEditMark[]) {
  view.dispatch({ effects: clearAiEdits.of(null) });
  marks.forEach((mark) => markAiEdit(view, mark));
}
