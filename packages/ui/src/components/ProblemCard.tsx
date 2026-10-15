// One problem as a card: difficulty, minutes, the Free pill, tags, state, and the actions that open
// a session, its last result, the answer or the set-up screen. Used by Home and the section page.
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { useApp } from '../app-context.ts';
import { useOpeners } from '../hooks.ts';
import { plural } from '../lib/format.ts';
import { progressFor, STATE_LABEL } from '../lib/progress.ts';
import { TagChips } from './TagChips.tsx';

export function ProblemCard({ problem: p, history }: { problem: ProblemSummary; history: readonly SessionSummary[] }) {
  const { go } = useApp();
  const { open, result } = useOpeners();
  const { state, best, attempts, openId, lastFinishedId } = progressFor(p, history);
  const { icon, label } = STATE_LABEL[state];
  const facts = [best === null ? '' : `best ${best} / 100`, attempts ? plural(attempts, 'attempt') : '']
    .filter(Boolean)
    .join(', ');
  return (
    <article className="card" aria-labelledby={`problem-${p.id}`}>
      <h3 id={`problem-${p.id}`}>{p.title}</h3>
      <p className="tags">
        <span className={`pill ${p.difficulty.toLowerCase()}`}>{p.difficulty}</span>
        <span className="pill">{p.minutes} min</span>
        {p.free && <span className="label free">Free</span>}
      </p>
      <p className={`state ${state}`}>
        <span aria-hidden="true">{icon}</span> {label}
        {facts && <span className="facts-inline">{facts}</span>}
      </p>
      <p className="summary">{p.summary}</p>
      <TagChips problem={p} />
      <div className="actions">
        {openId && (
          <button className="ghost" type="button" onClick={() => open(openId)}>
            Resume
          </button>
        )}
        {lastFinishedId && (
          <button className="ghost" type="button" onClick={() => result(lastFinishedId)}>
            Last result
          </button>
        )}
        <button className="ghost" type="button" onClick={() => go({ name: 'answer', problem: p })}>
          Answer
        </button>
        <button className="primary" type="button" onClick={() => go({ name: 'start', problem: p })}>
          Start
        </button>
      </div>
    </article>
  );
}
