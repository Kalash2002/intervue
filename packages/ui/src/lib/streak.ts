// Streak and the seven-day strip on Home, from finished sessions' start dates in local time. A day
// counts once however many sessions it has; an unfinished session never counts. Pure functions,
// no React.
import type { SessionSummary } from '@intervue/local-engine-client';

const DAY_MS = 24 * 60 * 60 * 1000;

/** Midnight (local) of the day the timestamp falls in, as a number of days. */
function dayOf(epochMs: number): number {
  const d = new Date(epochMs);
  return Math.round(new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime() / DAY_MS);
}

function finishedDays(history: readonly SessionSummary[]): Set<number> {
  return new Set(history.filter((s) => s.finished).map((s) => dayOf(s.startedAt)));
}

/** Current run ending today (or yesterday when today has none yet) and the best run ever. */
export function streak(history: readonly SessionSummary[]): { current: number; best: number } {
  const days = finishedDays(history);
  const today = dayOf(Date.now());
  let current = 0;
  for (let d = days.has(today) ? today : today - 1; days.has(d); d--) current++;
  let best = 0;
  for (const d of days) {
    if (days.has(d - 1)) continue;
    let run = 0;
    while (days.has(d + run)) run++;
    best = Math.max(best, run);
  }
  return { current, best };
}

/** The last seven calendar days, oldest first, with a weekday label and whether a session finished. */
export function weekStrip(history: readonly SessionSummary[]): { label: string; done: boolean }[] {
  const days = finishedDays(history);
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date();
    d.setDate(d.getDate() - 6 + i);
    return { label: d.toLocaleDateString([], { weekday: 'short' }), done: days.has(dayOf(d.getTime())) };
  });
}
