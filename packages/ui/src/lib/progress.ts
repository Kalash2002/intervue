// Dashboard numbers worked out from the session history, as the prototype's progressFor did. A
// problem is solved by an Interview session that passed every phase, with or without AI edits (ADR 0006).
// Also the section page's filter and the Home track cells; all pure, no React.
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { TRACKS, trackOf, type Track } from './tracks.ts';

export type ProblemState = 'solved' | 'progress' | 'attempted' | 'new';

/** How each state is shown, in the order the status filter lists them. */
export const STATE_LABEL: Record<ProblemState, { icon: string; label: string }> = {
  solved: { icon: '✓', label: 'Solved' },
  progress: { icon: '▶', label: 'In progress' },
  attempted: { icon: '◐', label: 'Attempted' },
  new: { icon: '○', label: 'Not started' },
};

export interface ProblemProgress {
  state: ProblemState;
  attempts: number;
  /** Best total of an Interview session, or null when there is none. */
  best: number | null;
  openId: string | null;
  lastFinishedId: string | null;
}

export function progressFor(problem: ProblemSummary, history: readonly SessionSummary[]): ProblemProgress {
  const mine = history.filter((s) => s.problemId === problem.id);
  const finished = mine.filter((s) => s.finished);
  // Best score, like solved, counts Interview sessions only; Practice never does (PLAN.md 2.8, ADR 0006).
  const totals = finished.filter((s) => s.type === 'INTERVIEW').map((s) => s.total ?? 0);
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

/** The problems that list `p` in their `coveredBy`, resolved against the catalogue; unknown ids are skipped. */
export function covering(p: ProblemSummary, all: readonly ProblemSummary[]): ProblemSummary[] {
  return p.coveredBy.flatMap((id) => all.find((q) => q.id === id) ?? []);
}

const RANK: Record<string, number> = { Easy: 0, Medium: 1, Hard: 2 };
const rank = (p: ProblemSummary) => RANK[p.difficulty] ?? 9;

export function byDifficulty(problems: readonly ProblemSummary[]): ProblemSummary[] {
  return [...problems].sort((a, b) => rank(a) - rank(b));
}

/** Track order, then difficulty, then title: the order of the grouped list and of "next up". */
export function byTrack(problems: readonly ProblemSummary[]): ProblemSummary[] {
  const order = (p: ProblemSummary) => {
    const t = trackOf(p.techniques);
    return t ? TRACKS.indexOf(t) : TRACKS.length;
  };
  return [...problems].sort((a, b) => order(a) - order(b) || rank(a) - rank(b) || a.title.localeCompare(b.title));
}

export interface TrackCell {
  track: Track;
  solved: number;
  total: number;
}

/** One cell per NeetCode group: the problems filed under it (first matching group, as the list) and how many are solved. */
export function trackCells(problems: readonly ProblemSummary[], history: readonly SessionSummary[]): TrackCell[] {
  return TRACKS.map((track) => {
    const inGroup = problems.filter((p) => trackOf(p.techniques) === track);
    const solved = inGroup.filter((p) => progressFor(p, history).state === 'solved').length;
    return { track, solved, total: inGroup.length };
  });
}

/** The first problem not yet solved, in track order; null when everything is solved. */
export function nextUp(problems: readonly ProblemSummary[], history: readonly SessionSummary[]): ProblemSummary | null {
  return byTrack(problems).find((p) => progressFor(p, history).state !== 'solved') ?? null;
}

export interface Filter {
  query: string;
  difficulty: readonly string[];
  status: readonly ProblemState[];
  tags: readonly string[];
  view: 'grouped' | 'flat';
}

export const EMPTY_FILTER: Filter = { query: '', difficulty: [], status: [], tags: [], view: 'grouped' };

/** AND across kinds, OR within a kind (PLAN.md 7.2); an empty kind matches everything. */
export function applyFilter(
  problems: readonly ProblemSummary[],
  history: readonly SessionSummary[],
  filter: Filter,
): ProblemSummary[] {
  const q = filter.query.trim().toLowerCase();
  return problems.filter((p) => {
    const text = [p.title, ...p.tags, ...p.techniques].join(' ').toLowerCase();
    if (q && !text.includes(q)) return false;
    if (filter.difficulty.length && !filter.difficulty.includes(p.difficulty)) return false;
    if (filter.status.length && !filter.status.includes(progressFor(p, history).state)) return false;
    return !filter.tags.length || filter.tags.some((t) => p.tags.includes(t));
  });
}

/** Tags by how many problems carry them, most common first, ties by name. */
export function topTags(problems: readonly ProblemSummary[]): { tag: string; count: number }[] {
  const counts = new Map<string, number>();
  for (const p of problems) for (const t of p.tags) counts.set(t, (counts.get(t) ?? 0) + 1);
  return [...counts]
    .map(([tag, count]) => ({ tag, count }))
    .sort((a, b) => b.count - a.count || a.tag.localeCompare(b.tag));
}
