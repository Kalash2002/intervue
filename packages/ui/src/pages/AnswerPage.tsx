// A problem's answer: the walkthrough as a reading column, with an aside that lists its sections,
// the problem's facts and the DSA questions it practises. Open from a problem card at any time
// and from the result page.
import { useEffect, useMemo, useRef, useState } from 'react';
import type { ProblemSummary } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { useCatalogue, useOpenQuestion } from '../hooks.ts';
import { covering } from '../lib/progress.ts';
import { Chip } from '../components/Chip.tsx';
import { CoveringChips } from '../components/CoveringChips.tsx';
import { Markdown } from '../components/Markdown.tsx';
import { TagChips } from '../components/TagChips.tsx';
import { plural } from '../lib/format.ts';
import { trackOf } from '../lib/tracks.ts';

interface Answer {
  text: string;
  figures: Record<string, string>;
}

const DSA_LINK = /\[([^\]]+)\]\(dsa:\/\/([a-z0-9-]+)\)/g;

export function AnswerPage({ problem }: { problem: ProblemSummary }) {
  const { client, go, notify } = useApp();
  const [answer, setAnswer] = useState<Answer | null>(null);
  const reading = useRef<HTMLDivElement>(null);
  const [sections, setSections] = useState<{ id: string; title: string }[]>([]);

  useEffect(() => {
    client.walkthrough(problem.id).then(setAnswer, failed(notify));
  }, [client, notify, problem.id]);

  // The contents list comes from the rendered headings, so it follows whatever the walkthrough has.
  useEffect(() => {
    const headings = [...(reading.current?.querySelectorAll('h2') ?? [])];
    setSections(
      headings.map((h, i) => {
        h.id = `section-${i}`;
        return { id: h.id, title: h.textContent ?? '' };
      }),
    );
  }, [answer]);

  const practise = useMemo(
    () => [...(answer?.text ?? '').matchAll(DSA_LINK)].map((m) => ({ title: m[1] ?? '', id: m[2] ?? '' })),
    [answer],
  );
  return (
    <div className="page with-aside answer">
      <header className="page-head">
        <div>
          <p className="eyebrow">Answer</p>
          <h1>{problem.title}</h1>
          <TagChips problem={problem} />
        </div>
        <div className="actions">
          <button className="primary" type="button" onClick={() => go({ name: 'start', problem })}>
            Start a session
          </button>
        </div>
      </header>
      <div className="reading" ref={reading}>
        {answer && <Markdown source={answer.text || 'No walkthrough yet.'} figures={answer.figures} wide />}
      </div>
      <AnswerAside sections={sections} problem={problem} practise={practise} />
    </div>
  );
}

function AnswerAside({
  sections,
  problem,
  practise,
}: {
  sections: { id: string; title: string }[];
  problem: ProblemSummary;
  practise: { id: string; title: string }[];
}) {
  const openQuestion = useOpenQuestion();
  const track = trackOf(problem.techniques);
  return (
    <aside className="aside">
      {sections.length > 0 && (
        <nav className="card toc" aria-label="On this page">
          <p className="eyebrow">On this page</p>
          <ol>
            {sections.map((s) => (
              <li key={s.id}>
                <button type="button" onClick={() => document.getElementById(s.id)?.scrollIntoView({ block: 'start' })}>
                  {s.title}
                </button>
              </li>
            ))}
          </ol>
        </nav>
      )}
      <div className="card" aria-label="This problem">
        <p className="eyebrow">This problem</p>
        <p className="tags">
          <span className={`pill ${problem.difficulty.toLowerCase()}`}>{problem.difficulty}</span>
          <span className="pill">{problem.minutes} min</span>
          <span className="pill">{plural(problem.phases, 'phase')}</span>
        </p>
        {track && <p className="summary">Topic group: {track.title}</p>}
      </div>
      {practise.length > 0 && (
        <div className="card" aria-label="Practise">
          <p className="eyebrow">Practise under DSA</p>
          <p className="chips">
            {practise.map((q) => (
              <Chip key={q.id} onClick={() => void openQuestion(q.id)}>
                {q.title}
              </Chip>
            ))}
          </p>
        </div>
      )}
      <UsedIn problem={problem} />
    </aside>
  );
}

/** The coding-round problems that practise this DSA question; each opens its Answer page. */
function UsedIn({ problem }: { problem: ProblemSummary }) {
  return problem.coveredBy.length ? <UsedInCards problem={problem} /> : null;
}

// Split from UsedIn so the catalogue is fetched only when there is something to show.
function UsedInCards({ problem }: { problem: ProblemSummary }) {
  const catalogue = useCatalogue();
  const usedIn = catalogue ? covering(problem, catalogue.problems) : [];
  if (!usedIn.length) return null;
  return (
    <div className="card" aria-label="Used in">
      <p className="eyebrow">Used in</p>
      <CoveringChips problems={usedIn} />
    </div>
  );
}
