// The DSA result page body: score with its parts, tests passed, time spent and the walkthrough.
// It must not reproduce the coding round's phases or timeline.
import { useEffect, useState } from 'react';
import { clockText, failed, Markdown, useApp, type SectionResultProps } from '@intervue/ui';

export function DsaResult({ session }: SectionResultProps) {
  const [open, setOpen] = useState(false);
  const result = session.result;
  if (!result) return <p className="hint">This session is not finished.</p>;
  const { score } = result;
  const total = result.finalResults.reduce((n, p) => n + p.passed + p.failed + p.timeouts + p.notRun, 0);
  const passed = result.finalResults.reduce((n, p) => n + p.passed, 0);
  return (
    <>
      <section className="scoreline" aria-label="Score">
        <div className="score">
          <span className="num">{score.total}</span>
          <span className="of">/ 100</span>
        </div>
        <div className="score-text">
          <h1>
            <span className={`label ${session.type.toLowerCase()}`}>{score.label}</span> {score.band}
          </h1>
          <p>{`Tests passed ${passed}/${total}`}</p>
          <p>Time spent {clockText(session.activeMs)}</p>
        </div>
      </section>
      {result.finalCompileError && <p className="bad">The final code did not compile, so no test ran.</p>}
      <section className="phase-result">
        <table className="tests">
          <tbody>
            {score.parts.map((part) => (
              <tr key={part.name}>
                <td>{part.name}</td>
                <td className="points">
                  {part.points} / {part.max}
                </td>
                <td>{part.note}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
      <button className="ghost" type="button" aria-expanded={open} onClick={() => setOpen(!open)}>
        Walkthrough
      </button>
      {open && <Walkthrough problemId={session.problemId} text={result.walkthrough} />}
    </>
  );
}

/** The stored text shows at once; the figures arrive with the problem's walkthrough. */
function Walkthrough({ problemId, text }: { problemId: string; text: string }) {
  const { client, notify } = useApp();
  const [figures, setFigures] = useState<Record<string, string>>({});
  useEffect(() => {
    client.walkthrough(problemId).then((w) => setFigures(w.figures), failed(notify));
  }, [client, notify, problemId]);
  return <Markdown source={text || 'No walkthrough yet.'} figures={figures} wide />;
}
