import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { afterEach, describe, expect, it, vi } from 'vitest';
import {
  applyFilter,
  dashboardStats,
  EMPTY_FILTER,
  nextUp,
  progressFor,
  topTags,
  trackCells,
} from '../src/lib/progress.ts';
import { streak, weekStrip } from '../src/lib/streak.ts';
import { TRACKS } from '../src/lib/tracks.ts';

const problem = (over: Partial<ProblemSummary>): ProblemSummary => ({
  id: 'p',
  section: 'coding-round',
  title: 'P',
  summary: '',
  difficulty: 'Easy',
  techniques: ['arrays'],
  tags: [],
  minutes: 30,
  phases: 3,
  free: false,
  ...over,
});

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

const solvedSession = (problemId: string, startedAt = 0): SessionSummary => ({
  id: `s-${problemId}-${startedAt}`,
  problemId,
  type: 'INTERVIEW',
  startedAt,
  finished: true,
  total: 90,
  label: 'Interview',
  solved: true,
  countsAsSolved: true,
});

describe('progress', () => {
  it('takes the best score from Interview sessions, assisted or not, never Practice (ADR 0006)', () => {
    const history = [
      finished(92, 'INTERVIEW', 'Interview'),
      finished(98, 'PRACTICE', 'Practice'),
      finished(60, 'INTERVIEW', 'Interview'),
      // Stored before ADR 0006 with the old label; still an Interview session.
      finished(95, 'INTERVIEW', 'Interview, assisted'),
    ];

    expect(progressFor(problem({}), history).best).toBe(95);
    expect(dashboardStats([problem({})], history).averageBest).toBe(95);
  });

  it('has no best score without an Interview session', () => {
    expect(progressFor(problem({}), [finished(98, 'PRACTICE', 'Practice')]).best).toBeNull();
  });
});

describe('tracks', () => {
  it('lists the 18 NeetCode groups in roadmap order', () => {
    expect(TRACKS).toHaveLength(18);
    expect(TRACKS[0]?.id).toBe('arrays-hashing');
    expect(TRACKS[17]?.id).toBe('bit-manipulation');
  });

  it('fills one cell per group from the solved problems techniques', () => {
    const problems = [
      problem({ id: 'a', techniques: ['hashing', 'arrays'] }),
      problem({ id: 'b', techniques: ['graphs', 'bfs'] }),
      problem({ id: 'c', techniques: ['graphs'] }),
      problem({ id: 'd', techniques: ['strings'] }),
    ];
    const cells = trackCells(problems, [solvedSession('a'), solvedSession('c')]);

    expect(cells).toHaveLength(18);
    expect(cells.find((c) => c.track.id === 'arrays-hashing')).toMatchObject({ solved: 1, total: 1 });
    expect(cells.find((c) => c.track.id === 'graphs')).toMatchObject({ solved: 1, total: 2 });
    expect(cells.find((c) => c.track.id === 'stack')).toMatchObject({ solved: 0, total: 0 });
    // `strings` is outside the 18 groups and counts in no cell.
    expect(cells.reduce((n, c) => n + c.total, 0)).toBe(3);
  });
});

describe('next up', () => {
  it('picks the first unsolved problem in track order, then difficulty, then title', () => {
    const problems = [
      problem({ id: 'hard-graph', title: 'Z', difficulty: 'Hard', techniques: ['graphs'] }),
      problem({ id: 'easy-graph', title: 'B', difficulty: 'Easy', techniques: ['graphs'] }),
      problem({ id: 'easy-graph-2', title: 'A', difficulty: 'Easy', techniques: ['graphs'] }),
      problem({ id: 'stack', difficulty: 'Easy', techniques: ['stack'] }),
    ];

    expect(nextUp(problems, [])?.id).toBe('stack');
    expect(nextUp(problems, [solvedSession('stack')])?.id).toBe('easy-graph-2');
    expect(
      nextUp(
        problems,
        problems.map((p) => solvedSession(p.id)),
      ),
    ).toBeNull();
  });
});

describe('filters', () => {
  const problems = [
    problem({
      id: 'a',
      title: 'Hold Exposure',
      difficulty: 'Easy',
      techniques: ['intervals'],
      tags: ['fintech', 'sorting'],
    }),
    problem({
      id: 'b',
      title: 'Corridor Router',
      difficulty: 'Medium',
      techniques: ['graphs'],
      tags: ['fintech', 'bfs'],
    }),
    problem({ id: 'c', title: 'Merchant Suggest', difficulty: 'Hard', techniques: ['tries'], tags: ['fintech'] }),
  ];
  const history = [solvedSession('a'), { ...solvedSession('b'), finished: false, countsAsSolved: false }];

  it('matches the query against title and tags, case-insensitively', () => {
    expect(applyFilter(problems, history, { ...EMPTY_FILTER, query: 'corridor' }).map((p) => p.id)).toEqual(['b']);
    expect(applyFilter(problems, history, { ...EMPTY_FILTER, query: 'BFS' }).map((p) => p.id)).toEqual(['b']);
  });

  it('ORs within a kind and ANDs across kinds', () => {
    const easyOrHard = applyFilter(problems, history, { ...EMPTY_FILTER, difficulty: ['Easy', 'Hard'] });
    expect(easyOrHard.map((p) => p.id)).toEqual(['a', 'c']);
    const easyOrHardAndSolved = applyFilter(problems, history, {
      ...EMPTY_FILTER,
      difficulty: ['Easy', 'Hard'],
      status: ['solved'],
    });
    expect(easyOrHardAndSolved.map((p) => p.id)).toEqual(['a']);
    expect(applyFilter(problems, history, { ...EMPTY_FILTER, status: ['progress', 'new'] }).map((p) => p.id)).toEqual([
      'b',
      'c',
    ]);
    expect(applyFilter(problems, history, { ...EMPTY_FILTER, tags: ['sorting', 'bfs'] }).map((p) => p.id)).toEqual([
      'a',
      'b',
    ]);
  });

  it('counts tags by frequency and keeps the most common first', () => {
    expect(topTags(problems).slice(0, 2)).toEqual([
      { tag: 'fintech', count: 3 },
      { tag: 'bfs', count: 1 },
    ]);
    expect(topTags(problems)).toHaveLength(3);
  });
});

describe('streak', () => {
  const noon = (daysAgo: number) => {
    const d = new Date(2026, 9, 15, 12);
    d.setDate(d.getDate() - daysAgo);
    return d.getTime();
  };
  afterEach(() => vi.useRealTimers());

  it('counts consecutive days with a finished session back from today, or from yesterday', () => {
    vi.useFakeTimers({ now: new Date(2026, 9, 15, 15) });
    expect(streak([])).toEqual({ current: 0, best: 0 });
    expect(streak([solvedSession('a', noon(0)), solvedSession('a', noon(1)), solvedSession('b', noon(2))])).toEqual({
      current: 3,
      best: 3,
    });
    // Nothing today yet: yesterday's run still counts.
    expect(streak([solvedSession('a', noon(1)), solvedSession('a', noon(2))])).toEqual({ current: 2, best: 2 });
    // A gap breaks the current streak but the best remembers the longer run.
    expect(
      streak([
        solvedSession('a', noon(0)),
        solvedSession('a', noon(3)),
        solvedSession('a', noon(4)),
        solvedSession('a', noon(5)),
      ]),
    ).toEqual({ current: 1, best: 3 });
    // Two sessions on one day are one day; an unfinished session does not count.
    expect(
      streak([
        solvedSession('a', noon(0)),
        solvedSession('b', noon(0) + 1),
        { ...solvedSession('c', noon(1)), finished: false },
      ]),
    ).toEqual({
      current: 1,
      best: 1,
    });
  });

  it('gives seven booleans ending today', () => {
    vi.useFakeTimers({ now: new Date(2026, 9, 15, 15) });
    const strip = weekStrip([solvedSession('a', noon(0)), solvedSession('a', noon(6)), solvedSession('a', noon(7))]);
    expect(strip).toHaveLength(7);
    expect(strip.map((d) => d.done)).toEqual([true, false, false, false, false, false, true]);
    expect(strip[6]?.label).toBe('Thu');
  });
});
