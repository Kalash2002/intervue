import { useEffect, useState } from 'react';
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { ProblemCard, useOpeners } from '../components/ProblemCard.tsx';
import { dateText } from '../lib/format.ts';
import { byDifficulty, dashboardStats } from '../lib/progress.ts';

export function Dashboard() {
  const { client, notify } = useApp();
  const [data, setData] = useState<{ problems: ProblemSummary[]; history: SessionSummary[] } | null>(null);

  useEffect(() => {
    Promise.all([client.problems(), client.history()])
      .then(([problems, history]) => setData({ problems: byDifficulty(problems), history }))
      .catch(failed(notify));
  }, [client, notify]);

  return (
    <main className="home">
      <section className="home-intro">
        <h1>Rehearse the AI-assisted coding round.</h1>
        <p>
          Interview sessions run against the clock and count towards solved problems. Practice sessions use a stopwatch
          and authored hints, and are labelled Practice.
        </p>
      </section>
      {data && <Stats problems={data.problems} history={data.history} />}
      {data && <Problems problems={data.problems} history={data.history} />}
      {data && <History problems={data.problems} history={data.history} />}
    </main>
  );
}

function Stats({ problems, history }: { problems: ProblemSummary[]; history: SessionSummary[] }) {
  const s = dashboardStats(problems, history);
  return (
    <section aria-label="Your progress">
      <h2>Your progress</h2>
      <dl className="stats">
        <Stat value={`${s.solved} of ${s.problems}`} label="Solved" />
        <Stat value={String(s.attempted)} label="Attempted, not yet solved" />
        <Stat value={String(s.untouched)} label="Not started" />
        <Stat value={s.averageBest === null ? '–' : `${s.averageBest} / 100`} label="Average of best scores" />
        <Stat value={String(s.finishedSessions)} label="Finished sessions" />
      </dl>
      <p className="hint">An Interview session that passes every phase counts as solved. Practice never does.</p>
    </section>
  );
}

function Stat({ value, label }: { value: string; label: string }) {
  return (
    <div>
      <dd>{value}</dd>
      <dt>{label}</dt>
    </div>
  );
}

function Problems({ problems, history }: { problems: ProblemSummary[]; history: SessionSummary[] }) {
  return (
    <section aria-label="Problems">
      <h2>Problems</h2>
      <div className="cards">
        {problems.map((p) => (
          <ProblemCard key={p.id} problem={p} history={history} />
        ))}
      </div>
    </section>
  );
}

function History({ problems, history }: { problems: ProblemSummary[]; history: SessionSummary[] }) {
  const { open, result } = useOpeners();
  const title = (id: string) => problems.find((p) => p.id === id)?.title ?? id;
  return (
    <section aria-label="Session history">
      <h2>Session history</h2>
      <div className="rows">
        {history.length === 0 && <div className="empty-row">No sessions yet. Start a problem above.</div>}
        {history.map((s) => (
          <div className="row" key={s.id}>
            <div>
              <h3>
                {title(s.problemId)}
                {s.finished && <span className="level">{s.total} / 100</span>}
              </h3>
              <p className="meta">
                <span className={`label ${s.type.toLowerCase()}`}>{s.label ?? labelOf(s)}</span> {dateText(s.startedAt)}
                , {s.finished ? 'finished' : 'in progress'}
              </p>
            </div>
            <div className="actions">
              <button className="ghost" type="button" onClick={() => (s.finished ? result(s.id) : open(s.id))}>
                {s.finished ? 'Result' : 'Resume'}
              </button>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

/** Unfinished sessions have no score label yet; show their type. */
function labelOf(s: SessionSummary): string {
  return s.type === 'PRACTICE' ? 'Practice' : 'Interview';
}
