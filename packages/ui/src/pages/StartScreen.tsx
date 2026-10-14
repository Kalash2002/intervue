import { useState } from 'react';
import type { CheckResult, ProblemSummary, SessionType } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { timed } from '../lib/timed.ts';

/** Interview or Practice, fixed once the clock starts (PLAN.md 2.8); the fast checks run first. */
export function StartScreen({ problem }: { problem: ProblemSummary }) {
  const { client, go, notify } = useApp();
  const [busy, setBusy] = useState(false);
  const [blocking, setBlocking] = useState<CheckResult[]>([]);

  async function start(type: SessionType) {
    setBusy(true);
    try {
      const report = await client.runHealth(true);
      if (!report.ready) {
        setBlocking(report.results.filter((r) => r.blocking && r.status === 'FAIL'));
        return;
      }
      go({ name: 'room', session: timed(await client.createSession(problem.id, type)) });
    } catch (error) {
      failed(notify)(error);
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="home">
      <header className="home-head">
        <button className="ghost back wordmark" type="button" onClick={() => go({ name: 'dashboard' })}>
          intervue
        </button>
      </header>
      <section className="home-intro">
        <h1>{problem.title}</h1>
        <p>{problem.summary}</p>
      </section>
      {blocking.length > 0 && <Blocking checks={blocking} onDoctor={() => go({ name: 'doctor', firstRun: false })} />}
      <div className="choices">
        <button className="choice" type="button" disabled={busy} onClick={() => start('INTERVIEW')}>
          <h2>Interview</h2>
          <p>
            {problem.minutes} minutes on the clock, no hints. Scored like the real round; a pass without applied AI
            edits counts as solved.
          </p>
        </button>
        <button className="choice" type="button" disabled={busy} onClick={() => start('PRACTICE')}>
          <h2>Practice</h2>
          <p>
            A stopwatch instead of a limit, and three levels of hints per phase. Labelled Practice; never counts as
            solved.
          </p>
        </button>
      </div>
      <p className="hint">The type cannot change once the session starts. The clock pauses while the app is closed.</p>
    </main>
  );
}

function Blocking({ checks, onDoctor }: { checks: CheckResult[]; onDoctor: () => void }) {
  return (
    <div className="notice" role="alert">
      <p>This machine is not ready for a session:</p>
      <ul>
        {checks.map((c) => (
          <li key={c.id}>
            <b>{c.title}</b>: {c.message}
          </li>
        ))}
      </ul>
      <button className="ghost small" type="button" onClick={onDoctor}>
        Open the Doctor
      </button>
    </div>
  );
}
