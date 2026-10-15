import { useState } from 'react';
import type { CheckResult, ProblemSummary, SessionType } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { timed } from '../lib/timed.ts';

/**
 * "Set up your session": the language, the session type (fixed once the clock starts, PLAN.md 2.8)
 * and, for Practice, whether the limit applies. The fast health checks run before the session.
 */
export function StartScreen({ problem }: { problem: ProblemSummary }) {
  const { client, go, notify } = useApp();
  const [type, setType] = useState<SessionType>('INTERVIEW');
  const [timedPractice, setTimedPractice] = useState(false);
  const [busy, setBusy] = useState(false);
  const [blocking, setBlocking] = useState<CheckResult[]>([]);

  async function start() {
    setBusy(true);
    try {
      const report = await client.runHealth(true);
      if (!report.ready) {
        setBlocking(report.results.filter((r) => r.blocking && r.status === 'FAIL'));
        return;
      }
      const view = await client.createSession(problem.id, type, type === 'INTERVIEW' || timedPractice);
      go({ name: 'overview', session: timed(view) });
    } catch (error) {
      failed(notify)(error);
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="home">
      <section className="home-intro">
        <p className="eyebrow">{problem.title}</p>
        <h1>Set up your session</h1>
        <p>{problem.summary}</p>
      </section>
      {blocking.length > 0 && <Blocking checks={blocking} onDoctor={() => go({ name: 'doctor', firstRun: false })} />}
      <label className="field">
        Language
        <select aria-label="Language" value="java" disabled>
          <option value="java">Java</option>
        </select>
      </label>
      <SessionOptions
        minutes={problem.minutes}
        type={type}
        setType={setType}
        timedPractice={timedPractice}
        setTimedPractice={setTimedPractice}
      />
      <p>
        <button className="primary" type="button" disabled={busy} onClick={start}>
          Start session
        </button>
      </p>
      <p className="hint">The type cannot change once the session starts. The clock pauses while the app is closed.</p>
    </main>
  );
}

function SessionOptions({
  minutes,
  type,
  setType,
  timedPractice,
  setTimedPractice,
}: {
  minutes: number;
  type: SessionType;
  setType: (t: SessionType) => void;
  timedPractice: boolean;
  setTimedPractice: (on: boolean) => void;
}) {
  return (
    <>
      <div className="choices" role="radiogroup" aria-label="Session type">
        <TypeCard
          type="INTERVIEW"
          chosen={type}
          onChoose={setType}
          title="Interview"
          text={`${minutes} minutes on the clock, no hints. Scored like the real round; a pass counts as solved, and using the assistant well is part of it.`}
        />
        <TypeCard
          type="PRACTICE"
          chosen={type}
          onChoose={setType}
          title="Practice"
          text="Three levels of hints per phase and helpful assistant rules. Labelled Practice; never counts as solved."
        />
      </div>
      {type === 'PRACTICE' && (
        <label className="switch">
          <input type="checkbox" checked={timedPractice} onChange={(e) => setTimedPractice(e.target.checked)} />
          Timed ({minutes} minutes); otherwise a stopwatch
        </label>
      )}
    </>
  );
}

function TypeCard(props: {
  type: SessionType;
  chosen: SessionType;
  onChoose: (t: SessionType) => void;
  title: string;
  text: string;
}) {
  const selected = props.type === props.chosen;
  return (
    <button
      className={selected ? 'choice selected' : 'choice'}
      type="button"
      role="radio"
      aria-checked={selected}
      onClick={() => props.onChoose(props.type)}
    >
      <h2>{props.title}</h2>
      <p>{props.text}</p>
    </button>
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
