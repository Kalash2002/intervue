// One section's page under Practice: its title and description from section.json, then its
// problems. Fed by the section registry; it never imports a section package.
import { useEffect, useState } from 'react';
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { ProblemCard } from '../components/ProblemCard.tsx';
import { byDifficulty } from '../lib/progress.ts';

interface Data {
  problems: ProblemSummary[];
  history: SessionSummary[];
}

export function SectionPage({ sectionId, tag }: { sectionId: string; tag?: string }) {
  const { client, notify, sections } = useApp();
  const section = sections.find((s) => s.id === sectionId);
  const [data, setData] = useState<Data | null>(null);

  useEffect(() => {
    Promise.all([client.problems(), client.history()])
      .then(([problems, history]) =>
        setData({
          problems: byDifficulty(problems.filter((p) => p.section === sectionId && (!tag || p.tags.includes(tag)))),
          history,
        }),
      )
      .catch(failed(notify));
  }, [client, notify, sectionId, tag]);

  if (!section) return <p className="loading">No section named {sectionId}.</p>;
  return (
    <main className="page">
      <header className="page-head">
        <div>
          <p className="eyebrow">Practice</p>
          <h1>{section.title}</h1>
          <p className="lede">{section.description}</p>
        </div>
      </header>
      {data && (
        <section aria-label="Problems" className="cards">
          {data.problems.map((p) => (
            <ProblemCard key={p.id} problem={p} history={data.history} />
          ))}
        </section>
      )}
    </main>
  );
}
