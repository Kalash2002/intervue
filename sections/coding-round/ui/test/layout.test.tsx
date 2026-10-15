// The room stylesheet and the room markup must agree on the grid tracks: a grid row reserved for a
// child that is never rendered left the editor in a thin strip (fixed in e779c67).
import roomCss from '../../../../packages/ui/src/styles/room.css?raw';
import { timed } from '@intervue/ui';
import { describe, expect, it, vi } from 'vitest';
import { SessionRoom } from '../src/room/SessionRoom.tsx';
import { renderWith, view } from './fixtures.tsx';

/** Track count of the first `.<name>` rule's grid template, ignoring spaces inside parentheses. */
function tracks(css: string, name: string, axis: 'rows' | 'columns'): number {
  const rule = new RegExp(`\\n\\.${name} \\{([^}]*)\\}`).exec(css)?.[1] ?? '';
  const list = new RegExp(`grid-template-${axis}:([^;]*);`).exec(rule)?.[1] ?? '';
  return list
    .replace(/\([^)]*\)/g, '()')
    .trim()
    .split(/\s+/).length;
}

const client = () => ({
  session: vi.fn(async () => view()),
  files: vi.fn(async () => []),
  aiStatus: vi.fn(async () => ({ developer: false, available: false, reason: 'off' })),
  chat: vi.fn(async () => []),
});

describe('the room layout', () => {
  it('gives the work area one grid column and the center one grid row per child', async () => {
    const { container } = renderWith(<SessionRoom session={timed(view())} />, client());

    expect(container.querySelector('.center')?.children.length).toBe(tracks(roomCss, 'center', 'rows'));
    expect(tracks(roomCss, 'center', 'rows')).toBe(3);
    expect(container.querySelector('.work')?.children.length).toBe(tracks(roomCss, 'work', 'columns'));
    expect(tracks(roomCss, 'work', 'columns')).toBe(5);
  });
});
