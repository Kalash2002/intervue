import { fireEvent, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { dsaQuickUi } from '../src/index.ts';
import { DsaResult } from '../src/DsaResult.tsx';
import { renderWith, view } from './fixtures.tsx';

const result = {
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
};
const finished = view({ finished: true, activeMs: 754_000, result });

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

  it('rounds score points to one decimal', () => {
    const rounded = {
      ...finished,
      result: {
        ...result,
        score: {
          ...result.score,
          parts: [{ name: 'Correctness', points: 68.57142857142857, max: 80, note: '6 of 7' }],
        },
      },
    };
    renderWith(<DsaResult session={rounded} />, {} as never);

    expect(screen.getByText('68.6 / 80')).toBeInTheDocument();
  });

  it('says the code did not compile when the final run had no tests', () => {
    const broken = {
      ...finished,
      result: {
        ...result,
        finalCompileError: true,
        finalResults: [{ ...result.finalResults[0], passed: 0, failed: 0 }],
      },
    };
    renderWith(<DsaResult session={broken} />, {} as never);

    expect(screen.getByText('Tests not run: the code did not compile in the final run')).toBeInTheDocument();
    expect(screen.queryByText(/Tests passed 0\/0/)).toBeNull();
  });

  it('is exported with the section identity', () => {
    expect(dsaQuickUi.id).toBe('dsa-quick');
    expect(dsaQuickUi.title).toBe('DSA');
  });
});
