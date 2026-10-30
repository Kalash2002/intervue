import { describe, expect, it } from 'vitest';
import { javaEditor } from '../src/index.ts';

describe('Java editor support', () => {
  it('is keyed by the language id the engine uses', () => {
    expect(javaEditor.id).toBe('java');
    expect(javaEditor.extensions).toEqual(['.java']);
  });

  it('completes the keywords a candidate types most', () => {
    const labels = javaEditor.words.map((w) => w.label);

    expect(labels).toEqual(expect.arrayContaining(['public', 'static', 'return', 'for', 'class', 'record']));
    expect(new Set(labels).size).toBe(labels.length);
  });

  it('offers snippets for the common shapes', () => {
    const snippets = javaEditor.words.filter((w) => w.type === 'text');

    expect(snippets.map((s) => s.label)).toEqual(expect.arrayContaining(['fori', 'foreach', 'sout', 'main']));
  });

  it('offers java.util types for import and no java.lang types', () => {
    const types = javaEditor.importableTypes ?? [];

    expect(types).toContainEqual({ name: 'ArrayList', pkg: 'java.util' });
    expect(types.every((t) => t.pkg === 'java.util')).toBe(true);
  });
});
