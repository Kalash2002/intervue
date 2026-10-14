import { fireEvent, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { WrittenReview } from '../src/result/WrittenReview.tsx';
import { renderWith } from './fixtures.tsx';

describe('the written review', () => {
  it('waits while the review is written, then shows it', async () => {
    const review = vi
      .fn()
      .mockResolvedValueOnce({ status: 'pending', text: null, reason: null })
      .mockResolvedValue({ status: 'ready', text: '## Verdict\nHire, because the runs were steady.', reason: null });
    renderWith(<WrittenReview sessionId="s1" pollMs={10} />, { review });

    expect(await screen.findByText(/Writing the review/)).toBeInTheDocument();
    expect(await screen.findByRole('heading', { name: 'Verdict' })).toBeInTheDocument();
    expect(screen.getByText('Hire, because the runs were steady.')).toBeInTheDocument();
  });

  it('says why when there is no route for a review', async () => {
    renderWith(<WrittenReview sessionId="s1" pollMs={10} />, {
      review: vi.fn(async () => ({
        status: 'unavailable' as const,
        text: null,
        reason: 'Assistant unavailable: sign-in arrives in Phase 1b',
      })),
    });

    expect(
      await screen.findByText('Review unavailable: Assistant unavailable: sign-in arrives in Phase 1b'),
    ).toBeInTheDocument();
  });

  it('is a tab on the result page', async () => {
    const { ResultParts } = await import('../src/result/ResultParts.tsx');
    const { view } = await import('./fixtures.tsx');
    const session = view({
      finished: true,
      result: {
        score: { total: 50, band: 'Partial', solved: false, label: 'Interview', parts: [] },
        finalResults: [],
        finalCompileError: false,
        report: {
          activeMs: 0,
          runs: 0,
          compileErrors: 0,
          prompts: 0,
          firstRunMs: -1,
          firstPromptMs: -1,
          longestGapWithoutRunMs: 0,
          longestPromptStreak: 0,
          phasesSkipped: 0,
        },
        walkthrough: '',
        followUps: [],
      },
    });
    renderWith(<ResultParts session={session} />, {
      review: vi.fn(async () => ({ status: 'ready' as const, text: '## Verdict\nHire.', reason: null })),
    });

    fireEvent.click(screen.getByRole('tab', { name: 'Written review' }));

    expect(await screen.findByRole('heading', { name: 'Verdict' })).toBeInTheDocument();
  });
});
