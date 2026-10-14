// The room stylesheet and the room markup must agree on the center column: a grid row reserved for a
// child that is never rendered left the editor in a thin strip (fixed in e779c67).
import roomCss from '../../../../packages/ui/src/styles/room.css?raw';
import { timed } from '@intervue/ui';
import { describe, expect, it, vi } from 'vitest';
import { SessionRoom } from '../src/room/SessionRoom.tsx';
import { renderWith, view } from './fixtures.tsx';

/** Track count of the first `.center` rule's grid-template-rows, ignoring spaces inside parentheses. */
function centerRows(css: string): number {
  const rule = /\n\.center \{([^}]*)\}/.exec(css)?.[1] ?? '';
  const rows = /grid-template-rows:([^;]*);/.exec(rule)?.[1] ?? '';
  return rows
    .replace(/\([^)]*\)/g, '()')
    .trim()
    .split(/\s+/).length;
}

describe('the room layout', () => {
  it('gives the center column one grid row per child', async () => {
    const client = {
      session: vi.fn(async () => view()),
      files: vi.fn(async () => []),
      aiStatus: vi.fn(async () => ({ developer: false, available: false, reason: 'off' })),
      chat: vi.fn(async () => []),
    };
    const { container } = renderWith(<SessionRoom session={timed(view())} />, client);

    const center = container.querySelector('.center');
    expect(center?.children.length).toBe(centerRows(roomCss));
    expect(centerRows(roomCss)).toBe(3);
  });
});
