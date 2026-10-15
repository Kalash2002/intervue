import { useEffect, useState, type ComponentType } from 'react';
import type { ProblemSummary, SessionView } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { TagChips } from '../components/TagChips.tsx';
import { timed } from '../lib/timed.ts';
import type { SectionResultProps } from '../section.ts';

/** A finished session: the section's result parts under a header with the problem's tags and the ways on. */
export function ResultPage({ session, Parts }: { session: SessionView; Parts?: ComponentType<SectionResultProps> }) {
  const { client, go, notify } = useApp();
  const [problem, setProblem] = useState<ProblemSummary | null>(null);

  useEffect(() => {
    client.problems().then((all) => setProblem(all.find((p) => p.id === session.problemId) ?? null), failed(notify));
  }, [client, notify, session.problemId]);

  return (
    <div className="page result">
      <header className="page-head">
        <div>
          <p className="eyebrow">Result</p>
          <h1>{session.title}</h1>
          <TagChips problem={{ section: session.section, tags: session.tags }} />
        </div>
        <div className="actions">
          <button
            className="ghost"
            type="button"
            onClick={() => go({ name: 'answer', problemId: session.problemId, title: session.title })}
          >
            Answer
          </button>
          <button className="ghost" type="button" onClick={() => go({ name: 'room', session: timed(session) })}>
            Open the workspace
          </button>
          <button
            className="primary"
            type="button"
            disabled={!problem}
            onClick={() => problem && go({ name: 'start', problem })}
          >
            Try again
          </button>
        </div>
      </header>
      <div className="result-body">
        {Parts ? <Parts session={session} /> : <p className="hint">No section UI for {session.section}.</p>}
      </div>
    </div>
  );
}
