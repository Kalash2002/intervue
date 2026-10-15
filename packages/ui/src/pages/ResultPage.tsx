import type { ComponentType } from 'react';
import type { SessionView } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { TopBar } from '../components/TopBar.tsx';
import { timed } from '../lib/timed.ts';
import type { SectionResultProps } from '../section.ts';

export function ResultPage({ session, Parts }: { session: SessionView; Parts: ComponentType<SectionResultProps> }) {
  const { client, go, notify } = useApp();

  const tryAgain = () =>
    client
      .problems()
      .then((problems) => {
        const problem = problems.find((p) => p.id === session.problemId);
        go(problem ? { name: 'start', problem } : { name: 'dashboard' });
      })
      .catch(failed(notify));

  return (
    <div className="result">
      <TopBar onHome={() => go({ name: 'dashboard' })}>
        <div className="title">{session.title}</div>
        <div className="spacer" />
        <button className="ghost" type="button" onClick={() => go({ name: 'answer', session })}>
          Answer
        </button>
        <button className="ghost" type="button" onClick={() => go({ name: 'room', session: timed(session) })}>
          Open the workspace
        </button>
        <button className="primary" type="button" onClick={tryAgain}>
          Try again
        </button>
      </TopBar>
      <div className="result-body">
        <Parts session={session} />
      </div>
    </div>
  );
}
