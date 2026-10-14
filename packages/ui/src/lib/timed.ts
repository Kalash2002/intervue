// The engine reports active time as of its reply; the room ticks forward from the moment the reply
// arrived. Pauses happen only while the app is closed, so a ticking clock never needs to stop.
import { useEffect, useState } from 'react';
import type { SessionView } from '@intervue/local-engine-client';

export interface TimedSession {
  view: SessionView;
  /** Date.now() when the engine's reply arrived. */
  receivedAt: number;
}

export function timed(view: SessionView): TimedSession {
  return { view, receivedAt: Date.now() };
}

/** Active ms of the session now, updated every second while it is open. */
export function useActiveMs({ view, receivedAt }: TimedSession): number {
  const [now, setNow] = useState(0);
  useEffect(() => {
    if (view.finished) return undefined;
    const timer = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(timer);
  }, [view.finished]);
  return view.finished ? view.activeMs : view.activeMs + Math.max(0, now - receivedAt);
}
