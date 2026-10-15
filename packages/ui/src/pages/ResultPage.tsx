import type { ComponentType } from 'react';
import type { ProblemSummary, SessionView } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { TagChips } from '../components/TagChips.tsx';
import { timed } from '../lib/timed.ts';
import type { SectionResultProps } from '../section.ts';

/** A finished session: the section's result parts under a header with the problem's tags and the ways on. */
export function ResultPage({ session, Parts }: { session: SessionView; Parts: ComponentType<SectionResultProps> }) {
  const { client, go, notify } = useApp();

  // The room reaches this page with the session alone, so the problem is looked up on demand.
  const withProblem = (then: (problem: ProblemSummary) => void) =>
    client.problems().then((all) => {
      const problem = all.find((p) => p.id === session.problemId);
      if (problem) then(problem);
      else notify('This problem is no longer installed.', 'bad');
    }, failed(notify));

  return (
    <div className="page result">
      <header className="page-head">
        <div>
          <p className="eyebrow">Result</p>
          <h1>{session.title}</h1>
          <TagChips problem={session} />
        </div>
        <div className="actions">
          <button
            className="ghost"
            type="button"
            onClick={() => withProblem((problem) => go({ name: 'answer', problem }))}
          >
            Answer
          </button>
          <button className="ghost" type="button" onClick={() => go({ name: 'room', session: timed(session) })}>
            Open the workspace
          </button>
          <button
            className="primary"
            type="button"
            onClick={() => withProblem((problem) => go({ name: 'start', problem }))}
          >
            Try again
          </button>
        </div>
      </header>
      <div className="result-body">
        <Parts session={session} />
      </div>
    </div>
  );
}
