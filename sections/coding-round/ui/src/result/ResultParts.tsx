// The coding round's result page body (section.json resultParts): score with its label, the strip,
// then phases and how the session went, the walkthrough, and follow-ups with notes.
import { useState } from 'react';
import type { SessionResult, SessionView } from '@intervue/local-engine-client';
import { clockText, failed, plural, useApp, type SectionResultProps } from '@intervue/ui';
import { ScoreLine, WalkthroughTab } from '@intervue/room';
import { TimelineStrip } from '../timeline/TimelineStrip.tsx';
import { WrittenReview } from './WrittenReview.tsx';

type Tab = 'Phases' | 'Written review' | 'Walkthrough' | 'Follow-ups and notes';

export function ResultParts({ session }: SectionResultProps) {
  const [tab, setTab] = useState<Tab>('Phases');
  const result = session.result;
  if (!result) return <p className="hint">This session is not finished.</p>;
  return (
    <>
      <CodingScoreLine session={session} result={result} />
      <div className="strip framed">
        <TimelineStrip view={session} activeMs={session.activeMs} />
      </div>
      <div className="tabs side result-tabs" role="tablist">
        {(['Phases', 'Written review', 'Walkthrough', 'Follow-ups and notes'] as const).map((t) => (
          <button
            key={t}
            role="tab"
            aria-selected={tab === t}
            className={tab === t ? 'tab active' : 'tab'}
            type="button"
            onClick={() => setTab(t)}
          >
            {t}
          </button>
        ))}
      </div>
      {tab === 'Phases' && <Phases session={session} result={result} />}
      {tab === 'Written review' && (
        <div className="rtab">
          <WrittenReview sessionId={session.id} />
        </div>
      )}
      {tab === 'Walkthrough' && (
        <div className="rtab">
          <WalkthroughTab problemId={session.problemId} text={result.walkthrough} />
        </div>
      )}
      {tab === 'Follow-ups and notes' && <FollowUps session={session} result={result} />}
    </>
  );
}

function CodingScoreLine({ session, result }: { session: SessionView; result: SessionResult }) {
  return (
    <ScoreLine session={session} score={result.score} suffix={result.score.solved ? ', all phases solved' : ''}>
      <p>
        Active time {clockText(session.activeMs)}
        {session.timed ? ` of ${session.minutes}:00` : ' on the stopwatch'}.
      </p>
      <p className="hint">
        {session.type === 'PRACTICE'
          ? 'Practice scores count correctness and phases only and never count as solved.'
          : 'The score counts tests, time and how often you ran your code.'}
      </p>
    </ScoreLine>
  );
}

function Phases({ session, result }: { session: SessionView; result: SessionResult }) {
  return (
    <>
      {result.finalResults.map((p) => {
        const passedAt = session.phasesPassed[String(p.phaseId)];
        const ok = p.passed > 0 && p.failed + p.timeouts + p.notRun === 0;
        const total = p.passed + p.failed + p.timeouts + p.notRun;
        return (
          <section className="phase-result" key={p.phaseId}>
            <h2>
              Phase {p.phaseId}: {p.title}
              <span className={`verdict ${ok ? 'good' : 'bad'}`}>{ok ? '✓ Passed' : '✗ Not passed'}</span>
            </h2>
            <p>
              {passedAt !== undefined ? `First passed at ${clockText(passedAt)}.` : 'Never passed during the session.'}{' '}
              Final run: {p.passed} of {total} tests pass.
            </p>
          </section>
        );
      })}
      {result.finalCompileError && <p className="bad">The final code did not compile, so no test ran.</p>}
      <HowYouWorked session={session} result={result} />
      <ScoreTable result={result} />
    </>
  );
}

function HowYouWorked({ session, result }: { session: SessionView; result: SessionResult }) {
  const r = result.report;
  const count = (type: string) => session.events.filter((e) => e.type === type).length;
  const hints = Object.values(session.hintsUsed).reduce((a, b) => a + b, 0);
  const fact = (label: string, value: string) => [
    <dt key={`${label}t`}>{label}</dt>,
    <dd key={`${label}d`}>{value}</dd>,
  ];
  return (
    <section className="phase-result" aria-label="How you worked">
      <h2>How you worked</h2>
      <dl className="facts">
        {fact('First run', r.firstRunMs < 0 ? 'never' : `at ${clockText(r.firstRunMs)}`)}
        {fact('Runs', `${r.runs}${r.compileErrors ? ` (${r.compileErrors} did not compile)` : ''}`)}
        {fact('Longest stretch without a run', clockText(r.longestGapWithoutRunMs))}
        {fact('Phases opened without passing', String(r.phasesSkipped))}
        {fact('Hints revealed', String(hints))}
        {fact('AI edits applied', `${session.aiEditsApplied}, ${session.aiEditsUndone} undone`)}
        {fact('Pauses (app closed)', String(count('pause')))}
      </dl>
    </section>
  );
}

function ScoreTable({ result }: { result: SessionResult }) {
  return (
    <section className="phase-result">
      <h2>How the score is made</h2>
      <table className="tests">
        <tbody>
          {result.score.parts.map((part) => (
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
  );
}

function FollowUps({ session, result }: { session: SessionView; result: SessionResult }) {
  const { client, notify } = useApp();
  const [notes, setNotes] = useState(session.notes);
  return (
    <div className="rtab">
      <div className="md wide">
        <h2 className="first">Answer these aloud</h2>
        <ol>
          {result.followUps.map((q) => (
            <li key={q}>{q}</li>
          ))}
        </ol>
        <h2>Your notes</h2>
        <p>What would you do differently? {plural(result.followUps.length, 'question')} above.</p>
      </div>
      <textarea
        className="notes"
        placeholder="Saved when you leave the box."
        value={notes}
        onChange={(e) => setNotes(e.target.value)}
        onBlur={() => client.saveNotes(session.id, notes).catch(failed(notify))}
      />
    </div>
  );
}
