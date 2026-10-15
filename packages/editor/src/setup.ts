// The editor every session file opens in: CodeMirror's basic setup, the language package's mode,
// tier 1 completion, inline diagnostics, locked ranges and AI-edit decorations.
import { autocompletion, type CompletionSource } from '@codemirror/autocomplete';
import { lintGutter } from '@codemirror/lint';
import { EditorState, type Extension } from '@codemirror/state';
import { EditorView, keymap, type KeyBinding } from '@codemirror/view';
import { basicSetup } from 'codemirror';
import { aiEdits } from './ai-edits.ts';
import { syntaxColours } from './highlight.ts';
import { lockedRanges } from './locked.ts';

export interface EditorOptions {
  /** The language package's CodeMirror support, such as `java()`. */
  language: Extension;
  completionSources: readonly CompletionSource[];
  readOnly: boolean;
  onChange: (doc: string) => void;
  onUndoAiEdit: (editId: string) => void;
  keys?: readonly KeyBinding[];
}

export function editorExtensions(options: EditorOptions): Extension[] {
  return [
    // Room shortcuts first, so Ctrl+S and Ctrl+Enter win over the default keymap.
    keymap.of([...(options.keys ?? [])]),
    basicSetup,
    syntaxColours,
    options.language,
    autocompletion({ override: [...options.completionSources] }),
    lintGutter(),
    lockedRanges,
    aiEdits(options.onUndoAiEdit),
    EditorState.readOnly.of(options.readOnly),
    EditorView.updateListener.of((update) => {
      if (update.docChanged) options.onChange(update.state.doc.toString());
    }),
  ];
}
