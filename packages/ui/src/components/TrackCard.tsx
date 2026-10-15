// A section's track: solved count, a bar and one cell per NeetCode group, the same shape on Home and
// on the section page.
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { dashboardStats, trackCells } from '../lib/progress.ts';

export function TrackCard({
  title,
  problems,
  history,
  onOpen,
  onCell,
}: {
  title: string;
  problems: readonly ProblemSummary[];
  history: readonly SessionSummary[];
  /** When given, the title is a button that opens the section. */
  onOpen?: () => void;
  onCell?: (trackId: string) => void;
}) {
  const stats = dashboardStats(problems, history);
  const cells = trackCells(problems, history);
  const percent = stats.problems ? Math.round((100 * stats.solved) / stats.problems) : 0;
  return (
    <article className="card track" aria-label={title}>
      <header className="track-head">
        {onOpen ? (
          <button className="ghost small" type="button" aria-label={`Open ${title}`} onClick={onOpen}>
            {title} →
          </button>
        ) : (
          <h3>{title}</h3>
        )}
        <span className="track-count">
          {stats.solved} of {stats.problems} solved
        </span>
      </header>
      <div className="bar" role="progressbar" aria-valuenow={percent} aria-valuemin={0} aria-valuemax={100}>
        <span style={{ width: `${percent}%` }} />
      </div>
      <ul className="cells" aria-label="Topic groups">
        {cells.map((c) => {
          const text = `${c.track.title}: ${c.solved} of ${c.total} solved`;
          const cls = ['cell', c.total ? 'has' : '', c.total && c.solved === c.total ? 'done' : ''].join(' ').trim();
          return (
            <li key={c.track.id} className={cls} title={text}>
              <button
                type="button"
                aria-label={text}
                disabled={!onCell || !c.total}
                onClick={() => onCell?.(c.track.id)}
              >
                <span style={{ height: c.total ? `${(100 * c.solved) / c.total}%` : '0%' }} />
              </button>
            </li>
          );
        })}
      </ul>
    </article>
  );
}
