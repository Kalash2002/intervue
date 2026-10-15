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
  it('takes the best score from unassisted interviews only (PLAN.md 2.8)', () => {
    const history = [
      finished(92, 'INTERVIEW', 'Interview, assisted'),
      finished(98, 'PRACTICE', 'Practice'),
      finished(60, 'INTERVIEW', 'Interview'),
    ];

    expect(progressFor(problem, history).best).toBe(60);
    expect(dashboardStats([problem], history).averageBest).toBe(60);
  });

  it('has no best score without an unassisted interview', () => {
    expect(progressFor(problem, [finished(98, 'PRACTICE', 'Practice')]).best).toBeNull();
  });
});
