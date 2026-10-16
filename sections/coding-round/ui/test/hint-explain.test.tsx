import { fireEvent, screen, waitFor } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { HintControl } from '@intervue/room';
import { renderWith, view } from './fixtures.tsx';

describe('explain this hint', () => {
  it('is offered only once a level is revealed', async () => {
    renderWith(<HintControl sessionId="s1" phase={1} used={0} channels={['ASK']} onRevealed={vi.fn()} />, {
      revealedHints: vi.fn(async () => []),
    });

    await screen.findByRole('button', { name: 'Show hint level 1 of 3' });
    expect(screen.queryByRole('button', { name: 'Explain this hint' })).toBeNull();
  });

  it('streams the explanation under the revealed hint', async () => {
    const explainHint = vi.fn(async (_id: string, _phase: number, onDelta: (t: string) => void) => {
      onDelta('Look where ');
      onDelta('the total is built.');
      return 'Look where the total is built.';
    });
    renderWith(<HintControl sessionId="s1" phase={2} used={1} channels={['ASK']} onRevealed={vi.fn()} />, {
      revealedHints: vi.fn(async () => ['Level one text']),
      explainHint,
      session: vi.fn(async () => view({ type: 'PRACTICE' })),
    });

    fireEvent.click(await screen.findByRole('button', { name: 'Explain this hint' }));

    // The streamed text is replaced by its rendered Markdown when the call ends.
    await waitFor(() => expect(screen.getByText('Look where the total is built.')).toBeInTheDocument());
    expect(explainHint).toHaveBeenCalledWith('s1', 2, expect.any(Function));
  });

  it('shows why when the assistant cannot answer', async () => {
    renderWith(<HintControl sessionId="s1" phase={1} used={1} channels={['ASK']} onRevealed={vi.fn()} />, {
      revealedHints: vi.fn(async () => ['Level one text']),
      explainHint: vi.fn(async () => Promise.reject(new Error('Assistant unavailable: sign-in arrives in Phase 1b'))),
      session: vi.fn(async () => view({ type: 'PRACTICE' })),
    });

    fireEvent.click(await screen.findByRole('button', { name: 'Explain this hint' }));

    expect(await screen.findByText('Assistant unavailable: sign-in arrives in Phase 1b')).toBeInTheDocument();
  });

  it('has no Explain button without the ASK channel', async () => {
    renderWith(<HintControl sessionId="s1" phase={1} used={1} channels={[]} onRevealed={vi.fn()} />, {
      revealedHints: vi.fn(async () => ['Level one text']),
    });

    await screen.findByText('Level one text');
    expect(screen.queryByRole('button', { name: 'Explain this hint' })).toBeNull();
  });
});
