import { fireEvent, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { dsaQuickUi } from '../src/index.ts';
import { DsaResult } from '../src/DsaResult.tsx';
import { renderWith, view } from './fixtures.tsx';

const finished = view({
  finished: true,
  activeMs: 754_000,
  result: {
    score: {
      total: 80,
      band: 'Good',
      solved: true,
      label: 'Interview',
      parts: [
        { name: 'Correctness', points: 80, max: 80, note: 'all tests' },
        { name: 'Time', points: 0, max: 20, note: 'over' },
      ],
    },
    finalResults: [
      { phaseId: 1, title: 'Solve', weight: 80, allOrNothing: false, passed: 7, failed: 1, timeouts: 0, notRun: 0 },
    ],
    finalCompileError: false,
    report: {
      activeMs: 754_000,
      runs: 2,
      compileErrors: 0,
      prompts: 0,
      firstRunMs: 1,
      firstPromptMs: -1,
      longestGapWithoutRunMs: 0,
      longestPromptStreak: 0,
      phasesSkipped: 0,
    },
    walkthrough: '# Use a hash map',
    followUps: [],
  },
});

describe('the DSA result', () => {
  it('shows score, parts, tests passed, time and a walkthrough on demand', () => {
    renderWith(<DsaResult session={finished} />, { walkthrough: vi.fn(async () => ({ figures: {} })) } as never);

    expect(screen.getByText('80')).toBeInTheDocument();
    expect(screen.getByText('Correctness')).toBeInTheDocument();
    expect(screen.getByText('Time')).toBeInTheDocument();
    expect(screen.getByText('Tests passed 7/8')).toBeInTheDocument();
    expect(screen.getByText(/12:34/)).toBeInTheDocument();
    expect(screen.queryByText('Use a hash map')).toBeNull();
    fireEvent.click(screen.getByRole('button', { name: 'Walkthrough' }));
    expect(screen.getByText('Use a hash map')).toBeInTheDocument();
  });

  it('is exported with the section identity', () => {
    expect(dsaQuickUi.id).toBe('dsa-quick');
    expect(dsaQuickUi.title).toBe('DSA');
  });
});
