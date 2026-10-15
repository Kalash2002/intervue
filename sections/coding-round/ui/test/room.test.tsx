import { EditorView } from '@codemirror/view';
import { act, fireEvent, screen, waitFor } from '@testing-library/react';
import type { EngineClient } from '@intervue/local-engine-client';
import { timed } from '@intervue/ui';
import { describe, expect, it, vi } from 'vitest';
import { HintControl } from '../src/room/HintControl.tsx';
import { SessionRoom } from '../src/room/SessionRoom.tsx';
import { renderWith, view } from './fixtures.tsx';

const BEFORE = 'public class Calc {\n  int twice(int n) {\n    return n + n + 1;\n  }\n}\n';
const AFTER = 'public class Calc {\n  int twice(int n) {\n    return n + n;\n  }\n}\n';

/** The demo button is disabled until its recorded streams have loaded. */
async function clickReplay() {
  const replay = await screen.findByRole('button', { name: 'Replay' });
  await waitFor(() => expect(replay).toBeEnabled());
  fireEvent.click(replay);
}

function roomClient(over: Partial<EngineClient> = {}): Partial<EngineClient> {
  let calc = BEFORE;
  return {
    // Developer mode with no CLI: the panel shows the reason and the recorded-reply demo.
    aiStatus: vi.fn(async () => ({ developer: true, available: false, reason: 'The Claude CLI was not found.' })),
    chat: vi.fn(async () => []),
    session: vi.fn(async () => view()),
    files: vi.fn(async () => [
      { path: 'Calc.java', readOnly: false },
      { path: 'Phase1Tests.java', readOnly: true },
    ]),
    readFile: vi.fn(async (_id: string, path: string) => ({ text: path === 'Calc.java' ? calc : 'class T {}' })),
    writeFile: vi.fn(async () => ({ ok: true })),
    symbols: vi.fn(async () => []),
    diagnostics: vi.fn(async () => []),
    fakeEditIds: vi.fn(async () => ['hold-exposure-phase1']),
    replayFakeEdits: vi.fn(async () => {
      calc = AFTER;
      return {
        replyId: 'r1',
        note: 'Fixed twice.',
        outcomes: [
          {
            id: 'r1:e1',
            replyId: 'r1',
            path: 'Calc.java',
            op: 'replace',
            status: 'APPLIED' as const,
            message: '',
            hunks: 1,
            beforeHash: 'a',
            afterHash: 'b',
          },
        ],
      };
    }),
    undoEdit: vi.fn(async () => {
      calc = BEFORE;
      return { id: 'r1:e1', undone: true, diff: null };
    }),
    ...over,
  };
}

describe('the splitters', () => {
  it('resize the side panel and the output pane by dragging', async () => {
    const { container } = renderWith(<SessionRoom session={timed(view())} />, roomClient());
    const work = container.querySelector<HTMLElement>('.work');

    const side = await screen.findByRole('separator', { name: 'Resize the side panel' });
    fireEvent.pointerDown(side, { pointerId: 1 });
    fireEvent.pointerMove(side, { clientX: window.innerWidth - 400 });
    fireEvent.pointerUp(side);
    fireEvent.pointerMove(side, { clientX: window.innerWidth - 350 });
    expect(work?.style.getPropertyValue('--right-w')).toBe('400px');

    const output = screen.getByRole('separator', { name: 'Resize the output pane' });
    fireEvent.pointerDown(output, { pointerId: 1 });
    fireEvent.pointerMove(output, { clientY: -1000 });
    // jsdom lays nothing out, so the center box is empty and the height falls to its 80px floor.
    expect(work?.style.getPropertyValue('--out-h')).toBe('80px');
  });
});

describe('the assistant panel', () => {
  it('replays a recorded reply, marks the edit and undoes it through the engine', async () => {
    const client = roomClient();
    const { container } = renderWith(<SessionRoom session={timed(view())} />, client);
    await waitFor(() => expect(container.querySelector('.cm-content')?.textContent).toContain('n + n + 1'));

    fireEvent.click(screen.getByRole('tab', { name: 'Assistant' }));
    await clickReplay();

    const undo = await waitFor(() => {
      const button = container.querySelector<HTMLButtonElement>('.cm-ai-undo');
      expect(button).not.toBeNull();
      return button as HTMLButtonElement;
    });
    expect(container.querySelector('.cm-ai-edit')?.textContent).toContain('return n + n;');
    fireEvent.click(undo);

    await waitFor(() => expect(client.undoEdit).toHaveBeenCalledWith('s1', 'r1:e1'));
    await waitFor(() => expect(container.querySelector('.cm-content')?.textContent).toContain('n + n + 1'));
  });
});

describe('the workspace', () => {
  it('saves unsaved typing before an undo reloads files', async () => {
    const client = roomClient();
    const { container } = renderWith(<SessionRoom session={timed(view())} />, client);
    await waitFor(() => expect(container.querySelector('.cm-content')?.textContent).toContain('n + n + 1'));
    fireEvent.click(screen.getByRole('tab', { name: 'Assistant' }));
    await clickReplay();
    const undo = await waitFor(() => {
      const button = container.querySelector<HTMLButtonElement>('.cm-ai-undo');
      expect(button).not.toBeNull();
      return button as HTMLButtonElement;
    });

    const editor = EditorView.findFromDOM(container.querySelector('.cm-editor') as HTMLElement);
    act(() => editor?.dispatch({ changes: { from: 0, insert: '// mine\n' } }));
    fireEvent.click(undo);

    await waitFor(() => expect(client.undoEdit).toHaveBeenCalled());
    expect(client.writeFile).toHaveBeenCalledWith('s1', 'Calc.java', expect.stringContaining('// mine'));
  });

  it('lists files the engine created after a replay', async () => {
    let created = false;
    const client = roomClient({
      files: vi.fn(async () => [
        { path: 'Calc.java', readOnly: false },
        ...(created ? [{ path: 'Helper.java', readOnly: false }] : []),
      ]),
      replayFakeEdits: vi.fn(async () => {
        created = true;
        return { replyId: 'r1', note: '', outcomes: [] };
      }),
    });
    renderWith(<SessionRoom session={timed(view())} />, client);

    fireEvent.click(await screen.findByRole('tab', { name: 'Assistant' }));
    await clickReplay();

    expect(await screen.findByRole('button', { name: /Helper\.java/ })).toBeInTheDocument();
  });
});

describe('hints', () => {
  it('are not offered in an Interview session', async () => {
    renderWith(<SessionRoom session={timed(view({ type: 'INTERVIEW' }))} />, roomClient());

    await screen.findByRole('tab', { name: 'Brief' });
    expect(screen.queryByRole('tab', { name: 'Hints' })).toBeNull();
  });

  it('reveal the three levels of a phase in order in Practice', async () => {
    const revealHint = vi.fn(async (_id: string, phase: number) => {
      const level = revealHint.mock.calls.length;
      return { phase, level, text: `Level ${level} text` };
    });
    renderWith(<HintControl sessionId="s1" phase={1} used={0} onRevealed={vi.fn()} />, {
      revealHint,
      revealedHints: vi.fn(async () => []),
      session: vi.fn(async () => view({ type: 'PRACTICE' })),
    });

    for (const level of [1, 2, 3]) {
      fireEvent.click(await screen.findByRole('button', { name: `Show hint level ${level} of 3` }));
      expect(await screen.findByText(`Level ${level} text`)).toBeInTheDocument();
    }

    expect(screen.queryByRole('button', { name: /Show hint/ })).toBeNull();
    expect(revealHint).toHaveBeenCalledTimes(3);
  });
});
