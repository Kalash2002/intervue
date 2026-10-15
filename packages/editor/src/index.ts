export { aiEdits, changedRange, clearAiEdits, markAiEdit, type AiEditMark } from './ai-edits.ts';
export { symbolCompletionSource, wordCompletionSource } from './completion.ts';
export { showDiagnostics, toDiagnostics } from './diagnostics.ts';
export { lockedRanges, setLockedRanges, type Range } from './locked.ts';
export { editorExtensions, type EditorOptions } from './setup.ts';
export { languageServer } from './lsp/index.ts';
export { languageFor, type LanguageEditor } from './language.ts';
