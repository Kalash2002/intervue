// Tier 1 completion (PLAN.md 4.5): project symbols the engine extracts on save, plus the language
// package's keywords and snippets. Both sources are plain CodeMirror completion sources.
import type { Completion, CompletionContext, CompletionResult, CompletionSource } from '@codemirror/autocomplete';
import type { SourceSymbol } from '@intervue/local-engine-client';

const WORD = /\w*/;

function wordResult(context: CompletionContext, options: readonly Completion[]): CompletionResult | null {
  const word = context.matchBefore(WORD);
  if (!word || (word.from === word.to && !context.explicit)) return null;
  return { from: word.from, options, validFor: /^\w*$/ };
}

const SYMBOL_TYPES: Record<string, string> = {
  class: 'class',
  interface: 'interface',
  record: 'class',
  enum: 'enum',
  method: 'method',
  field: 'property',
};

/** Reads the symbols at completion time, so a refresh after save needs no reconfiguration. */
export function symbolCompletionSource(symbols: () => readonly SourceSymbol[]): CompletionSource {
  return (context) =>
    wordResult(
      context,
      symbols().map((s) => ({ label: s.name, type: SYMBOL_TYPES[s.kind] ?? 'variable', detail: s.detail })),
    );
}

export function wordCompletionSource(words: readonly Completion[]): CompletionSource {
  return (context) => wordResult(context, words);
}
