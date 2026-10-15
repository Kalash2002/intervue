// The palette gate: every token the stylesheets rely on exists in both themes, and text colours
// read at 4.5:1 or better on their background (docs/design/palette.md, "Rules").
import { readFileSync } from 'node:fs';
import { join } from 'node:path';
import { describe, expect, it } from 'vitest';

// vitest runs with the package as cwd; import.meta.url is not a file URL under jsdom.
const css = readFileSync(join(process.cwd(), 'src/styles/tokens.css'), 'utf8');

function block(selector: string): Map<string, string> {
  const start = css.indexOf(selector);
  const body = css.slice(css.indexOf('{', start) + 1, css.indexOf('}', start));
  return new Map([...body.matchAll(/(--[\w-]+):\s*([^;]+);/g)].map((m) => [m[1] ?? '', (m[2] ?? '').trim()]));
}

function luminance(hex: string): number {
  const channel = (c: number) => (c <= 0.03928 ? c / 12.92 : ((c + 0.055) / 1.055) ** 2.4);
  const [r = 0, g = 0, b = 0] = [1, 3, 5].map((i) => channel(parseInt(hex.slice(i, i + 2), 16) / 255));
  return 0.2126 * r + 0.7152 * g + 0.0722 * b;
}

function contrast(a: string, b: string): number {
  const [hi = 0, lo = 0] = [luminance(a), luminance(b)].sort((x, y) => y - x);
  return (hi + 0.05) / (lo + 0.05);
}

const REQUIRED = [
  '--bg',
  '--panel',
  '--panel-2',
  '--input',
  '--line',
  '--outline',
  '--text',
  '--text-2',
  '--muted',
  '--accent',
  '--accent-ink',
  '--link',
  '--select',
  '--select-ink',
  '--pass',
  '--pass-bg',
  '--warn',
  '--warn-bg',
  '--fail',
  '--fail-bg',
  '--chat-bg',
  '--chat-in',
  '--chat-in-ink',
  '--chat-out',
  '--chat-out-ink',
  '--prompt',
  '--wash',
];
const TEXT = ['--text', '--text-2', '--muted', '--link', '--pass', '--warn', '--fail', '--prompt'];
const SYNTAX = ['--syn-keyword', '--syn-type', '--syn-string', '--syn-number', '--syn-def', '--syn-meta'];

describe.each([
  ['dark', ':root {'],
  ['light', ":root[data-theme='light'] {"],
])('%s theme', (name, selector) => {
  const tokens = block(selector);
  const ratio = (fg: string, bg: string) => contrast(tokens.get(fg) ?? '', tokens.get(bg) ?? '');

  it('defines every token the stylesheets use', () => {
    for (const token of REQUIRED) expect(tokens.has(token), token).toBe(true);
  });

  it('keeps text and syntax colours at 4.5:1 on the background and on cards', () => {
    const rows: string[] = [];
    for (const token of [...TEXT, ...SYNTAX]) {
      for (const surface of ['--bg', '--panel']) {
        rows.push(`${name} ${token} on ${surface}: ${ratio(token, surface).toFixed(2)}`);
        expect(ratio(token, surface), `${token} on ${surface}`).toBeGreaterThanOrEqual(4.5);
      }
    }
    expect(ratio('--accent-ink', '--accent'), 'button text').toBeGreaterThanOrEqual(4.5);
    expect(ratio('--chat-out-ink', '--chat-out'), 'outgoing bubble').toBeGreaterThanOrEqual(4.5);
    expect(ratio('--chat-in-ink', '--chat-in'), 'incoming bubble').toBeGreaterThanOrEqual(4.5);
    if (process.env['PRINT_CONTRAST']) console.info(rows.join('\n'));
  });
});
