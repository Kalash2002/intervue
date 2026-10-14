// Dashboard numbers worked out from the session history, as the prototype's progressFor did. A
// problem is solved only by an unassisted Interview session that passed every phase (PLAN.md 2.8).
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';

export type ProblemState = 'solved' | 'progress' | 'attempted' | 'new';

export interface ProblemProgress {
  state: ProblemState;
  attempts: number;
  /** Best finished total, or null when nothing is finished. */
  best: number | null;
  openId: string | null;
  lastFinishedId: string | null;
}

export function progressFor(problem: ProblemSummary, history: readonly SessionSummary[]): ProblemProgress {
  const mine = history.filter((s) => s.problemId === problem.id);
  const finished = mine.filter((s) => s.finished);
  const totals = finished.map((s) => s.total ?? 0);
  const open = mine.find((s) => !s.finished);
  const solved = finished.some((s) => s.countsAsSolved);
  const state: ProblemState = solved ? 'solved' : open ? 'progress' : finished.length ? 'attempted' : 'new';
  return {
    state,
    attempts: mine.length,
    best: totals.length ? Math.max(...totals) : null,
    openId: open?.id ?? null,
    lastFinishedId: finished[0]?.id ?? null,
  };
}

export interface DashboardStats {
  solved: number;
  attempted: number;
  untouched: number;
  problems: number;
  averageBest: number | null;
  finishedSessions: number;
}

export function dashboardStats(
  problems: readonly ProblemSummary[],
  history: readonly SessionSummary[],
): DashboardStats {
  const all = problems.map((p) => progressFor(p, history));
  const solved = all.filter((p) => p.state === 'solved').length;
  const untouched = all.filter((p) => p.state === 'new').length;
  const bests = all.map((p) => p.best).filter((b): b is number => b !== null);
  return {
    solved,
    untouched,
    attempted: all.length - solved - untouched,
    problems: all.length,
    averageBest: bests.length ? Math.round(bests.reduce((a, b) => a + b, 0) / bests.length) : null,
    finishedSessions: history.filter((s) => s.finished).length,
  };
}

const RANK: Record<string, number> = { Easy: 0, Medium: 1, Hard: 2 };

export function byDifficulty(problems: readonly ProblemSummary[]): ProblemSummary[] {
  return [...problems].sort((a, b) => (RANK[a.difficulty] ?? 9) - (RANK[b.difficulty] ?? 9));
}
