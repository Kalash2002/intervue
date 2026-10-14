import { screen, within } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { ResultParts } from '../src/result/ResultParts.tsx';
import { event, renderWith, view } from './fixtures.tsx';

const finished = (label: string) =>
  view({
    type: label === 'Practice' ? 'PRACTICE' : 'INTERVIEW',
    finished: true,
    activeMs: 600_000,
    hintsUsed: { '1': 2 },
    aiEditsApplied: 3,
    aiEditsUndone: 1,
    events: [
      event(1, 'pause', 100_000),
      event(2, 'resume', 100_000),
      event(3, 'aiEdit', 200_000, { id: 'r1:e1' }),
      event(4, 'aiEditUndo', 210_000, { id: 'r1:e1' }),
      event(5, 'pause', 300_000),
      event(6, 'resume', 300_000),
    ],
    result: {
      score: { total: 64, band: 'Partial', solved: false, label, parts: [] },
      finalResults: [
        { phaseId: 1, title: 'Fix', weight: 20, allOrNothing: true, passed: 3, failed: 0, timeouts: 0, notRun: 0 },
      ],
      finalCompileError: false,
      report: {
        activeMs: 600_000,
        runs: 4,
        compileErrors: 1,
        prompts: 0,
        firstRunMs: 60_000,
        firstPromptMs: -1,
        longestGapWithoutRunMs: 120_000,
        longestPromptStreak: 0,
        phasesSkipped: 0,
      },
      walkthrough: '# How to solve it',
      followUps: ['Why a map?'],
    },
  });

describe('result parts', () => {
  it.each(['Interview', 'Practice'])('show the %s label', (label) => {
    renderWith(<ResultParts session={finished(label)} />, { saveNotes: vi.fn() });

    expect(within(screen.getByLabelText('Score')).getByText(label)).toBeInTheDocument();
  });

  it('count pauses, hints and AI edits applied and undone', () => {
    renderWith(<ResultParts session={finished('Practice')} />, { saveNotes: vi.fn() });

    const facts = screen.getByLabelText('How you worked');
    expect(within(facts).getByText('Pauses (app closed)').nextSibling).toHaveTextContent('2');
    expect(within(facts).getByText('Hints revealed').nextSibling).toHaveTextContent('2');
    // The counts come from the engine's view, not from re-counting events here.
    expect(within(facts).getByText('AI edits applied').nextSibling).toHaveTextContent('3, 1 undone');
  });
});
