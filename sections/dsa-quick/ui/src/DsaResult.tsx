// The DSA result page body: score with its parts, tests passed, time spent and the walkthrough.
// It must not reproduce the coding round's phases or timeline.
import { useState } from 'react';
import { clockText, type SectionResultProps } from '@intervue/ui';
import { ScoreLine, totalHints, WalkthroughTab } from '@intervue/room';

export function DsaResult({ session }: SectionResultProps) {
  const [open, setOpen] = useState(false);
  const result = session.result;
  if (!result) return <p className="hint">This session is not finished.</p>;
  const { score } = result;
  const total = result.finalResults.reduce((n, p) => n + p.passed + p.failed + p.timeouts + p.notRun, 0);
  const passed = result.finalResults.reduce((n, p) => n + p.passed, 0);
  const hints = totalHints(session);
  return (
    <>
      <ScoreLine session={session} score={score}>
        <p>
          {total === 0 ? 'Tests not run: the code did not compile in the final run' : `Tests passed ${passed}/${total}`}
        </p>
        <p>Hints used {hints}</p>
        <p>Time spent {clockText(session.activeMs)}</p>
      </ScoreLine>
      {result.finalCompileError && <p className="bad">The final code did not compile, so no test ran.</p>}
      <section className="phase-result">
        <table className="tests">
          <tbody>
            {score.parts.map((part) => (
              <tr key={part.name}>
                <td>{part.name}</td>
                <td className="points">
                  {Math.round(part.points * 10) / 10} / {part.max}
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
      {open && <WalkthroughTab problemId={session.problemId} text={result.walkthrough} />}
    </>
  );
}
