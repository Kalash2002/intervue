import { useApp } from '../app-context.ts';
import { plural } from '../lib/format.ts';
import type { TimedSession } from '../lib/timed.ts';

/**
 * The overview before the first phase: how many phases and minutes, what each phase is called, and
 * one note on using the assistant. Shown once, right after the session is created.
 */
export function OverviewPage({ session }: { session: TimedSession }) {
  const { go } = useApp();
  const { view } = session;
  return (
    <main className="home">
      <section className="home-intro">
        <p className="eyebrow">{view.title}</p>
        <h1>
          {plural(view.phases, 'phase')}, {view.minutes} minutes
        </h1>
        <p>
          {view.timed ? 'The clock is running against the limit.' : 'A stopwatch counts the time; there is no limit.'}
        </p>
      </section>
      <ol className="overview-phases">
        {view.phaseTitles.map((title, i) => (
          <li key={title}>
            <span className="pill">Phase {i + 1}</span> {title}
          </li>
        ))}
      </ol>
      <p className="notice-soft">
        The assistant is part of the round. Ask it to explain code or check an idea; for code, state your approach and
        name the method you want written. Interviewers watch how you drive it.
      </p>
      <p>
        <button className="primary" type="button" onClick={() => go({ name: 'room', session })}>
          Start phase 1
        </button>
      </p>
    </main>
  );
}
