import { useCallback, useEffect, useState } from 'react';
import type { CheckResult } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';

const STATUS_ICON = { PASS: '✓', WARN: '!', FAIL: '✗' } as const;

/** PLAN.md 6.4 and 6.5: every check with its result, a full re-run, and a plain-text report to copy. */
export function DoctorPage({ firstRun }: { firstRun: boolean }) {
  const { client, go, notify } = useApp();
  const [results, setResults] = useState<CheckResult[]>([]);
  const [running, setRunning] = useState(firstRun);

  // Callers set `running` first; the effect cannot, because the first launch starts running.
  const runAll = useCallback(() => {
    client
      .runHealth(false)
      .then((report) => setResults(report.results))
      .catch(failed(notify))
      .finally(() => setRunning(false));
  }, [client, notify]);

  useEffect(() => {
    if (firstRun) {
      runAll();
    } else {
      client.health().then(setResults).catch(failed(notify));
    }
  }, [client, firstRun, notify, runAll]);

  const copy = () =>
    navigator.clipboard
      ?.writeText(reportText(results))
      .then(() => notify('Report copied.', 'good'))
      .catch(failed(notify));

  return (
    <main className="home">
      <header className="page-head">
        <p className="eyebrow">Doctor</p>
        <button className="primary" type="button" disabled={running} onClick={() => go({ name: 'dashboard' })}>
          Continue
        </button>
      </header>
      <Intro firstRun={firstRun} />
      <CheckList results={results} running={running} />
      <p>
        <button
          className="ghost"
          type="button"
          disabled={running}
          onClick={() => {
            setRunning(true);
            runAll();
          }}
        >
          Run all checks again
        </button>{' '}
        <button className="ghost" type="button" disabled={results.length === 0} onClick={copy}>
          Copy report
        </button>
      </p>
    </main>
  );
}

function Intro({ firstRun }: { firstRun: boolean }) {
  return (
    <section className="home-intro">
      <h1>Is this machine ready?</h1>
      <p>
        {firstRun && 'The first launch checks everything once and measures this machine; it takes about 20 seconds. '}
        Blocking checks must pass before a session starts; warnings only change how a session behaves.
      </p>
    </section>
  );
}

function CheckList({ results, running }: { results: CheckResult[]; running: boolean }) {
  return (
    <div className="rows" aria-busy={running}>
      {results.map((r) => (
        <div className="row" key={r.id}>
          <div>
            <h3>
              <span className={r.status === 'PASS' ? 'good' : 'bad'}>{STATUS_ICON[r.status]}</span> {r.title}
              {!r.blocking && <span className="level">warning only</span>}
            </h3>
            <p>{r.message}</p>
          </div>
          <div className="meta">{r.measured}</div>
        </div>
      ))}
      {running && <div className="empty-row">Running the checks…</div>}
    </div>
  );
}

/** Statuses and measured values only; never paths with the user name, tokens or session content. */
function reportText(results: readonly CheckResult[]): string {
  const lines = results.map((r) => `${r.status.padEnd(4)} ${r.id}: ${r.measured ?? ''}`.trimEnd());
  return [`Intervue Doctor report, ${new Date().toISOString()}`, `Agent: ${navigator.userAgent}`, ...lines].join('\n');
}
