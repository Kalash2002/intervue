// The session on an active-time axis, as the prototype's rhythm strip: runs as bars, phase passes as
// flags, plus what Phase 1a adds: hints, AI edits and pauses (the app was closed there).
import type { EventView, SessionView } from '@intervue/local-engine-client';
import { clockText } from '@intervue/ui';

function mark(e: EventView, pos: (t: number) => string) {
  const at = clockText(e.tMs);
  const style = { left: pos(e.tMs) };
  switch (e.type) {
    case 'run': {
      const broken = e.data.compileError === true;
      const failedTests = Number(e.data.failed ?? 0);
      const kind = broken ? 'run broken' : failedTests > 0 ? 'run fail' : 'run';
      const label = broken ? 'did not compile' : `${String(e.data.passed)} passed, ${failedTests} failed`;
      return <div key={e.seq} className={kind} style={style} title={`${at} run ${String(e.data.target)}: ${label}`} />;
    }
    case 'phasePassed':
      return (
        <div key={e.seq} className="flag" style={style} title={`Phase ${String(e.data.phase)} passed at ${at}`}>
          P{String(e.data.phase)}
        </div>
      );
    case 'hint':
      return <div key={e.seq} className="hint" style={style} title={`${at} hint, phase ${String(e.data.phase)}`} />;
    case 'aiEdit':
      return <div key={e.seq} className="edit" style={style} title={`${at} AI edit ${String(e.data.path)}`} />;
    case 'pause':
      return <div key={e.seq} className="pause" style={style} title={`App closed at ${at}`} />;
    default:
      return null;
  }
}

export function TimelineStrip({ view, activeMs }: { view: SessionView; activeMs: number }) {
  const limit = view.minutes * 60_000;
  const span = Math.max(limit, activeMs * 1.02);
  const pos = (t: number) => `${((100 * Math.min(t, span)) / span).toFixed(2)}%`;
  const ticks = Array.from({ length: Math.floor(span / 600_000) + 1 }, (_, i) => i * 10);
  return (
    <div className="strip-track">
      {ticks.map((m) => (
        <div key={`t${m}`} className="tick" style={{ left: pos(m * 60_000) }}>
          <span>{m}m</span>
        </div>
      ))}
      {view.type === 'INTERVIEW' && span > limit && (
        <div className="limit" style={{ left: pos(limit) }} title="Time limit" />
      )}
      {view.events.map((e) => mark(e, pos))}
      {!view.finished && <div className="now" style={{ left: pos(activeMs) }} />}
    </div>
  );
}
