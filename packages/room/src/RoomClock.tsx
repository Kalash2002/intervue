// The room header's clock, shared by every section's room.
import type { SessionView } from '@intervue/local-engine-client';
import { clockText } from '@intervue/ui';

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
