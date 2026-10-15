import { useEffect, useState } from 'react';
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { ThemeButton } from '../components/TopBar.tsx';
import { dateText, plural } from '../lib/format.ts';
import { byDifficulty, dashboardStats, progressFor, type ProblemState } from '../lib/progress.ts';
import { timed } from '../lib/timed.ts';

const STATE_LABEL: Record<ProblemState, [string, string]> = {
  solved: ['✓', 'Solved'],
  progress: ['▶', 'In progress'],
  attempted: ['◐', 'Attempted'],
  new: ['○', 'Not started'],
};

function useOpeners() {
  const { client, go, notify } = useApp();
  return {
    open: (id: string) => client.session(id).then((view) => go({ name: 'room', session: timed(view) }), failed(notify)),
    result: (id: string) => client.session(id).then((view) => go({ name: 'result', session: view }), failed(notify)),
  };
}

export function Dashboard() {
  const { client, go, notify } = useApp();
  const [data, setData] = useState<{ problems: ProblemSummary[]; history: SessionSummary[] } | null>(null);

  useEffect(() => {
    Promise.all([client.problems(), client.history()])
      .then(([problems, history]) => setData({ problems: byDifficulty(problems), history }))
      .catch(failed(notify));
  }, [client, notify]);

  return (
    <main className="home">
      <header className="home-head">
        <span className="wordmark">intervue</span>
        <span>
          <button className="ghost" type="button" onClick={() => go({ name: 'doctor', firstRun: false })}>
            Doctor
          </button>{' '}
          <ThemeButton />
        </span>
      </header>
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

function ProblemCard({ problem: p, history }: { problem: ProblemSummary; history: SessionSummary[] }) {
  const { go } = useApp();
  const { open, result } = useOpeners();
  const { state, best, attempts, openId, lastFinishedId } = progressFor(p, history);
  const [icon, label] = STATE_LABEL[state];
  const facts = [best === null ? '' : `best ${best} / 100`, attempts ? plural(attempts, 'attempt') : '']
    .filter(Boolean)
    .join(', ');
  return (
    <article className="card" aria-labelledby={`problem-${p.id}`}>
      <h3 id={`problem-${p.id}`}>{p.title}</h3>
      <p className="tags">
        <span className={`pill ${p.difficulty.toLowerCase()}`}>{p.difficulty}</span>
        <span className="pill">{p.minutes} min</span>
      </p>
      <p className={`state ${state}`}>
        <span aria-hidden="true">{icon}</span> {label}
        {facts && <span className="facts-inline">{facts}</span>}
      </p>
      <p className="summary">{p.summary}</p>
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
        <button className="ghost" type="button" onClick={() => go({ name: 'answer', problemId: p.id, title: p.title })}>
          Answer
        </button>
        <button className="primary" type="button" onClick={() => go({ name: 'start', problem: p })}>
          Start
        </button>
      </div>
    </article>
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
