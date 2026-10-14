import type { SessionView } from '@intervue/local-engine-client';
import { clockText } from '@intervue/ui';

/** One dot per phase in the header: filled when passed, ringed when open, hollow when locked. */
export function ProgressDots({ view }: { view: SessionView }) {
  return (
    <ol className="dots" aria-label="Phase progress">
      {Array.from({ length: view.phases }, (_, i) => i + 1).map((n) => {
        const passedAt = view.phasesPassed[String(n)];
        const state = passedAt !== undefined ? 'passed' : n <= view.unlocked ? 'open' : 'locked';
        const tip = passedAt !== undefined ? `passed at ${clockText(passedAt)}` : state;
        return <li key={n} className={state} aria-label={`Phase ${n}: ${state}`} title={`Phase ${n}: ${tip}`} />;
      })}
    </ol>
  );
}

/** A timed session counts down from the limit and runs over in red; an untimed one is a stopwatch. */
export function RoomClock({ view, activeMs }: { view: SessionView; activeMs: number }) {
  const limit = view.minutes * 60_000;
  if (view.finished) {
    return (
      <div className="clock done" title="Time used">
        {clockText(activeMs)}
      </div>
    );
  }
  if (!view.timed) {
    return (
      <div className="clock" title="Practice stopwatch">
        {clockText(activeMs)}
      </div>
    );
  }
  const over = activeMs > limit;
  return (
    <div className={over ? 'clock over' : 'clock'} title={over ? 'Over time' : 'Time left'}>
      {over ? `+${clockText(activeMs - limit)}` : clockText(limit - activeMs)}
    </div>
  );
}
