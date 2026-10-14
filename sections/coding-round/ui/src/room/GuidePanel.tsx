import { useState } from 'react';
import type { SessionView } from '@intervue/local-engine-client';
import { Markdown } from '@intervue/ui';
import type { RoomActions } from './useRoomActions.ts';

/** The phase being worked on: the first open phase not yet passed, else the last one. */
export function currentPhase(view: SessionView): number {
  for (let n = 1; n <= view.unlocked; n++) {
    if (view.phasesPassed[String(n)] === undefined) return n;
  }
  return Math.min(view.unlocked, view.phases);
}

/** The latest run of exactly this phase's tests, as "k of n passing". */
function passingPill(view: SessionView, phase: number): string {
  const runs = view.events.filter((e) => e.type === 'run' && e.data.target === `phase:${phase}`);
  const last = runs.at(-1);
  if (!last) return 'Not run yet';
  if (last.data.compileError === true) return 'Did not compile';
  const passed = Number(last.data.passed ?? 0);
  return `${passed} of ${passed + Number(last.data.failed ?? 0)} passing`;
}

/** The brief's first sentence as plain text, without its heading or emphasis and code marks. */
function firstSentence(markdown: string | null): string {
  const text = (markdown ?? '')
    .split('\n')
    .filter((l) => l.trim() !== '' && !l.startsWith('#'))
    .join(' ')
    .replace(/\*\*|`/g, '');
  return text.split(/(?<=[.!?])\s/)[0] ?? '';
}

/**
 * The Guide tab: each phase opens with an intro card (name, one sentence, Start, Skip phase); then
 * the phase text, the tests that must pass with a passing pill, and Run tests with its output here
 * as well as in the output pane.
 */
export function GuidePanel({ view, actions }: { view: SessionView; actions: RoomActions }) {
  const [started, setStarted] = useState<Set<number>>(new Set());
  const phase = currentPhase(view);
  const title = view.phaseTitles[phase - 1] ?? `Phase ${phase}`;
  const ran = view.events.some((e) => e.type === 'run' && e.data.target === `phase:${phase}`);
  if (!view.finished && !ran && !started.has(phase)) {
    return (
      <section className="phase-card" aria-label={`Phase ${phase}: ${title}`}>
        <p className="eyebrow">
          Phase {phase} of {view.phases}
        </p>
        <h2>{title}</h2>
        <p>{firstSentence(view.brief[phase])}</p>
        <button className="primary" type="button" onClick={() => setStarted(new Set(started).add(phase))}>
          Start
        </button>{' '}
        {phase < view.phases && (
          <button className="ghost" type="button" onClick={actions.skip}>
            Skip phase
          </button>
        )}
      </section>
    );
  }
  const last = actions.output.at(-1);
  return (
    <div className="guide">
      <Markdown source={view.brief[phase] ?? ''} />
      <p className="guide-tests">
        <span>Phase {phase} tests must pass</span> <span className="pill">{passingPill(view, phase)}</span>
      </p>
      <button
        className="primary small"
        type="button"
        disabled={view.finished || actions.busy}
        onClick={() => actions.run(`phase:${phase}`)}
      >
        Run tests
      </button>
      {last && <pre className="output-text guide-output">{last.text}</pre>}
    </div>
  );
}
