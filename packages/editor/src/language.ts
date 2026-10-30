// What a language package's editor binding provides (languages/<id>/editor); the room picks one
// by the open file's extension.
import type { Completion } from '@codemirror/autocomplete';
import type { Extension } from '@codemirror/state';
import type { ImportableType } from './completion.ts';

export interface LanguageEditor {
  id: string;
  /** File extensions with the dot, such as ".java". */
  extensions: readonly string[];
  language: () => Extension;
  /** Keywords and snippets for tier 1 completion. */
  words: readonly Completion[];
  /** Library types whose completion also adds the import. */
  importableTypes?: readonly ImportableType[];
}

export function languageFor(languages: readonly LanguageEditor[], path: string): LanguageEditor | undefined {
  return languages.find((l) => l.extensions.some((ext) => path.endsWith(ext)));
}
