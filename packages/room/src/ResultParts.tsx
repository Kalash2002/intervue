// Result-page pieces both sections show: the score header and the problem's walkthrough.
import { useEffect, useState, type ReactNode } from 'react';
import type { SessionView } from '@intervue/local-engine-client';
import { Markdown, failed, useApp } from '@intervue/ui';

/** Hints revealed over all phases of a session. */
export function totalHints(view: SessionView): number {
  return Object.values(view.hintsUsed).reduce((a, b) => a + b, 0);
}

/** The stored text shows at once; the figures arrive with the problem's walkthrough. */
export function WalkthroughTab({ problemId, text }: { problemId: string; text: string }) {
  const { client, notify } = useApp();
  const [figures, setFigures] = useState<Record<string, string>>({});
  useEffect(() => {
    client.walkthrough(problemId).then((w) => setFigures(w.figures), failed(notify));
  }, [client, notify, problemId]);
  return <Markdown source={text || 'No walkthrough yet.'} figures={figures} wide />;
}

/** The big score, its label and band; `suffix` follows the band and `children` are the lines below. */
export function ScoreLine({
  session,
  score,
  suffix,
  children,
}: {
  session: SessionView;
  score: { total: number; label: string; band: string };
  suffix?: string;
  children: ReactNode;
}) {
  return (
    <section className="scoreline" aria-label="Score">
      <div className="score">
        <span className="num">{score.total}</span>
        <span className="of">/ 100</span>
      </div>
      <div className="score-text">
        <h1>
          <span className={`label ${session.type.toLowerCase()}`}>{score.label}</span> {score.band}
          {suffix}
        </h1>
        {children}
      </div>
    </section>
  );
}
