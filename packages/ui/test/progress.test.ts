import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { describe, expect, it } from 'vitest';
import { dashboardStats, progressFor } from '../src/lib/progress.ts';

const problem = { id: 'p', title: 'P', summary: '', difficulty: 'Easy', minutes: 30, phases: 3 } as ProblemSummary;

const finished = (total: number, type: SessionSummary['type'], label: string): SessionSummary => ({
  id: `${label}-${total}`,
  problemId: 'p',
  type,
  startedAt: 0,
  finished: true,
  total,
  label,
  solved: false,
  countsAsSolved: false,
});

describe('progress', () => {
  it('takes the best score from Interview sessions, assisted or not, never Practice (ADR 0006)', () => {
    const history = [
      finished(92, 'INTERVIEW', 'Interview'),
      finished(98, 'PRACTICE', 'Practice'),
      finished(60, 'INTERVIEW', 'Interview'),
    ];

    expect(progressFor(problem, history).best).toBe(92);
    expect(dashboardStats([problem], history).averageBest).toBe(92);
  });

  it('has no best score without an Interview session', () => {
    expect(progressFor(problem, [finished(98, 'PRACTICE', 'Practice')]).best).toBeNull();
  });
});
