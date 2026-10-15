// One section's page under Practice: its description, its track card, the filter bar and the list
// grouped by NeetCode topic (or flat), with "next up" and the stat tiles in the aside. Fed by the
// section registry; it never imports a section package. Filters are kept per section while the app runs.
import { useEffect, useState } from 'react';
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { FilterBar } from '../components/FilterBar.tsx';
import { ProblemCard } from '../components/ProblemCard.tsx';
import { TrackCard } from '../components/TrackCard.tsx';
import {
  applyFilter,
  byDifficulty,
  dashboardStats,
  EMPTY_FILTER,
  nextUp,
  progressFor,
  topTags,
  type Filter,
} from '../lib/progress.ts';
import { TRACKS, trackOf, type Track } from '../lib/tracks.ts';

interface Data {
  problems: ProblemSummary[];
  history: SessionSummary[];
}

const OTHER: Track = { id: 'other', title: 'Other', techniques: [] };
const remembered = new Map<string, Filter>();

/** Drops the remembered filters; tests call it between cases. */
export function forgetFilters() {
  remembered.clear();
}

function initialFilter(sectionId: string, tag?: string): Filter {
  const kept = remembered.get(sectionId) ?? EMPTY_FILTER;
  return tag ? { ...EMPTY_FILTER, view: kept.view, tags: [tag] } : kept;
}

export function SectionPage({ sectionId, tag, track }: { sectionId: string; tag?: string; track?: string }) {
  const { client, notify, go, sections } = useApp();
  const section = sections.find((s) => s.id === sectionId);
  const [data, setData] = useState<Data | null>(null);
  const [filter, setFilter] = useState<Filter>(() => initialFilter(sectionId, tag));

  useEffect(() => {
    remembered.set(sectionId, filter);
  }, [sectionId, filter]);

  useEffect(() => {
    Promise.all([client.problems(), client.history()])
      .then(([problems, history]) => setData({ problems: problems.filter((p) => p.section === sectionId), history }))
      .catch(failed(notify));
  }, [client, notify, sectionId]);

  if (!section) return <p className="loading">No section named {sectionId}.</p>;
  const shown = data ? applyFilter(data.problems, data.history, filter) : [];
  const random = () => {
    const unsolved = shown.filter((p) => progressFor(p, data?.history ?? []).state !== 'solved');
    const pick = unsolved[Math.floor(Math.random() * unsolved.length)];
    if (pick) go({ name: 'start', problem: pick });
    else notify('Nothing unsolved matches the filters.');
  };
  return (
    <main className="page with-aside">
      <header className="page-head">
        <div>
          <p className="eyebrow">Practice</p>
          <h1>{section.title}</h1>
          <p className="lede">{section.description}</p>
        </div>
      </header>
      {data && (
        <div className="section-grid">
          <TrackCard title={section.title} problems={data.problems} history={data.history} />
          <FilterBar filter={filter} tags={topTags(data.problems)} onChange={setFilter} onRandom={random} />
          <p className="hint">
            {shown.length} of {data.problems.length} problems
          </p>
          {filter.view === 'flat' ? (
            <div className="cards">
              {byDifficulty(shown).map((p) => (
                <ProblemCard key={p.id} problem={p} history={data.history} />
              ))}
            </div>
          ) : (
            <Grouped problems={shown} history={data.history} openOnly={track} />
          )}
        </div>
      )}
      {data && <Aside {...data} />}
    </main>
  );
}

function Grouped({ problems, history, openOnly }: Data & { openOnly?: string }) {
  const groups = [...TRACKS, OTHER]
    .map((t) => ({
      track: t,
      items: byDifficulty(problems.filter((p) => (trackOf(p.techniques) ?? OTHER).id === t.id)),
    }))
    .filter((g) => g.items.length > 0);
  return (
    <div className="groups">
      {groups.map(({ track, items }) => {
        const solved = items.filter((p) => progressFor(p, history).state === 'solved').length;
        return (
          <details key={track.id} className="group" open={!openOnly || openOnly === track.id}>
            <summary>
              <span className="group-title">{track.title}</span>
              <span className="group-count">
                {solved}/{items.length}
              </span>
              <span className="bar">
                <span style={{ width: `${(100 * solved) / items.length}%` }} />
              </span>
            </summary>
            <div className="cards">
              {items.map((p) => (
                <ProblemCard key={p.id} problem={p} history={history} />
              ))}
            </div>
          </details>
        );
      })}
    </div>
  );
}

function Aside({ problems, history }: Data) {
  const { go } = useApp();
  const next = nextUp(problems, history);
  const s = dashboardStats(problems, history);
  return (
    <aside className="aside">
      {next && (
        <div className="card" aria-label="Next up">
          <p className="eyebrow">Next up</p>
          <h3>{next.title}</h3>
          <p className="summary">
            {next.difficulty}, {next.minutes} minutes
          </p>
          <div className="actions">
            <button className="primary" type="button" onClick={() => go({ name: 'start', problem: next })}>
              Start
            </button>
          </div>
        </div>
      )}
      <dl className="stats" aria-label="Your progress">
        <Stat value={`${s.solved} of ${s.problems}`} label="Solved" />
        <Stat value={String(s.attempted)} label="Attempted, not yet solved" />
        <Stat value={String(s.untouched)} label="Not started" />
        <Stat value={s.averageBest === null ? '–' : `${s.averageBest} / 100`} label="Average of best scores" />
        <Stat value={String(s.finishedSessions)} label="Finished sessions" />
      </dl>
      <p className="hint">An Interview session that passes every phase counts as solved. Practice never does.</p>
    </aside>
  );
}

function Stat({ value, label }: { value: string; label: string }) {
  return (
    <div>
      <dd>{value}</dd>
      <dt>{label}</dt>
    </div>
  );
}
