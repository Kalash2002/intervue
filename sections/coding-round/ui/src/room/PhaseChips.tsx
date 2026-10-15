import type { SessionView } from '@intervue/local-engine-client';
import { clockText } from '@intervue/ui';

export function PhaseChips({ view }: { view: SessionView }) {
  return (
    <ol className="phases">
      {Array.from({ length: view.phases }, (_, i) => i + 1).map((n) => {
        const passedAt = view.phasesPassed[String(n)];
        const state = passedAt !== undefined ? 'passed' : n <= view.unlocked ? 'open' : 'locked';
        const tip =
          passedAt !== undefined
            ? `Passed at ${clockText(passedAt)}`
            : state === 'open'
              ? 'Open'
              : 'Opens when the phase before it passes';
        return (
          <li key={n} className={state} title={tip}>
            <span className="n">{n}</span>
            Phase {n}
            {passedAt !== undefined && ' ✓'}
          </li>
        );
      })}
    </ol>
  );
}

/** Interview counts down from the limit and runs over in red; Practice is a stopwatch. */
export function RoomClock({ view, activeMs }: { view: SessionView; activeMs: number }) {
  const limit = view.minutes * 60_000;
  if (view.finished) {
    return (
      <div className="clock done" title="Time used">
        {clockText(activeMs)}
      </div>
    );
  }
  if (view.type === 'PRACTICE') {
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
