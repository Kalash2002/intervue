// What the editor needs from the Java package (PLAN.md 4.5): the CodeMirror mode, keywords and
// snippets. Project symbols come from the engine's symbol extractor, not from here.
import { snippetCompletion, type Completion } from '@codemirror/autocomplete';
import { java } from '@codemirror/lang-java';
import type { LanguageEditor } from '@intervue/editor';

const KEYWORDS = [
  'abstract', 'boolean', 'break', 'byte', 'case', 'catch', 'char', 'class', 'continue', 'default', 'do',
  'double', 'else', 'enum', 'extends', 'final', 'finally', 'float', 'for', 'if', 'implements', 'import',
  'instanceof', 'int', 'interface', 'long', 'new', 'null', 'private', 'protected', 'public', 'record',
  'return', 'short', 'static', 'super', 'switch', 'this', 'throw', 'throws', 'true', 'false', 'try', 'var',
  'void', 'while', 'yield',
]; // prettier-ignore

const SNIPPETS: Completion[] = [
  snippetCompletion('for (int ${i} = 0; ${i} < ${n}; ${i}++) {\n\t${}\n}', { label: 'fori', detail: 'index loop' }),
  snippetCompletion('for (${Type} ${item} : ${items}) {\n\t${}\n}', { label: 'foreach', detail: 'for-each loop' }),
  snippetCompletion('System.out.println(${});', { label: 'sout', detail: 'print a line' }),
  snippetCompletion('public static void main(String[] args) {\n\t${}\n}', { label: 'main', detail: 'entry point' }),
  snippetCompletion('Map<${K}, ${V}> ${map} = new HashMap<>();', { label: 'hashmap', detail: 'new HashMap' }),
  snippetCompletion('List<${T}> ${list} = new ArrayList<>();', { label: 'arraylist', detail: 'new ArrayList' }),
].map((s) => ({ ...s, type: 'text' }));

export const javaEditor: LanguageEditor = {
  id: 'java',
  extensions: ['.java'],
  language: java,
  words: [...KEYWORDS.map((label): Completion => ({ label, type: 'keyword' })), ...SNIPPETS],
};
