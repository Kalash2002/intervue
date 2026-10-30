// Tier 1 completion (PLAN.md 4.5): project symbols the engine extracts on save, plus the language
// package's keywords and snippets. Both sources are plain CodeMirror completion sources.
import {
  pickedCompletion,
  type Completion,
  type CompletionContext,
  type CompletionResult,
  type CompletionSource,
} from '@codemirror/autocomplete';
import { Transaction, type Text } from '@codemirror/state';
import type { EditorView } from '@codemirror/view';
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

export interface ImportableType {
  name: string;
  pkg: string;
}

const escape = (text: string) => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

/** Where a new import line goes: after the last import, else after `package`, else the top. */
function importEdit(doc: Text, pkg: string, name: string): { at: number; text: string } | null {
  const text = doc.toString();
  // Any import of this simple name, from any package, wins: a second one would not compile.
  if (new RegExp(`^\\s*import\\s+(static\\s+)?[\\w.]+\\.${escape(name)}\\s*;`, 'm').test(text)) return null;
  if (new RegExp(`^\\s*import\\s+${escape(pkg)}\\.\\*\\s*;`, 'm').test(text)) return null;
  const line = `import ${pkg}.${name};\n`;
  const lastOf = (re: RegExp) => [...text.matchAll(re)].pop();
  const found = lastOf(/^[ \t]*import\s[^\n]*$/gm) ?? lastOf(/^[ \t]*package\s[^\n]*$/gm);
  if (!found) return { at: 0, text: `${line}\n` };
  const end = found.index + found[0].length;
  const isPackage = /^\s*package\s/.test(found[0]);
  const body = isPackage ? `\n${line}` : line;
  return end >= text.length ? { at: end, text: `\n${body}` } : { at: end + 1, text: body };
}

/** Accepting a type inserts its name and, in the same transaction, its import if the file lacks one. */
function importingApply(pkg: string) {
  return (view: EditorView, completion: Completion, from: number, to: number) => {
    const edit = importEdit(view.state.doc, pkg, completion.label);
    const changes = [...(edit ? [{ from: edit.at, insert: edit.text }] : []), { from, to, insert: completion.label }];
    const shift = edit && edit.at <= from ? edit.text.length : 0;
    view.dispatch({
      changes,
      selection: { anchor: from + completion.label.length + shift },
      annotations: [pickedCompletion.of(completion), Transaction.userEvent.of('input.complete')],
    });
  };
}

/**
 * Offers JDK types the file may not import yet; accepting one adds the import. `taken` names the
 * project's own types, which win: importing a same-named JDK type would clash with them.
 */
export function importCompletionSource(
  types: readonly ImportableType[],
  taken: () => readonly string[] = () => [],
): CompletionSource {
  const options = types.map((t): Completion => ({
    label: t.name,
    type: 'class',
    detail: t.pkg,
    apply: importingApply(t.pkg),
  }));
  return (context) => {
    // On an unfinished import or package line a new import would corrupt it.
    if (/^\s*(import|package)(\s|$)/.test(context.state.doc.lineAt(context.pos).text)) return null;
    const own = new Set(taken());
    return wordResult(
      context,
      options.filter((o) => !own.has(o.label)),
    );
  };
}
