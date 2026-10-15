// The section list: menu order, and no id twice.
import { expect, test } from 'vitest';
import { sectionUis } from '../src/index.ts';

test('lists coding-round then dsa-quick, each once', () => {
  const ids = sectionUis.map((section) => section.id);
  expect(ids).toEqual(['coding-round', 'dsa-quick']);
  expect(new Set(ids).size).toBe(ids.length);
});
