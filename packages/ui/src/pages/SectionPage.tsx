// One section's page under Practice: its description, its track card, the filter bar and the list
// grouped by NeetCode topic (or flat), with "next up" and the stat tiles in the aside. Fed by the
// section registry; it never imports a section package. The filter is kept per section in
// sessionStorage, so it survives navigation and dies with the window.
import { useEffect, useState } from 'react';
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { useApp } from '../app-context.ts';
import { ContinueCard } from '../components/ContinueCard.tsx';
import { FilterBar } from '../components/FilterBar.tsx';
import { ProblemCard } from '../components/ProblemCard.tsx';
import { TrackCard } from '../components/TrackCard.tsx';
import { useCatalogue, type Catalogue } from '../hooks.ts';
import {
  applyFilter,
  byDifficulty,
  dashboardStats,
  EMPTY_FILTER,
  nextUp,
  progressFor,
  STATE_LABEL,
  topTags,
  type Filter,
  type ProblemState,
} from '../lib/progress.ts';
import { TRACKS, trackOf, type Track } from '../lib/tracks.ts';

const OTHER: Track = { id: 'other', title: 'Other', techniques: [] };
const storageKey = (sectionId: string) => `intervue.filter.${sectionId}`;

function storedFilter(sectionId: string): Filter {
  try {
    const raw = sessionStorage.getItem(storageKey(sectionId));
    const stored = raw ? (JSON.parse(raw) as Partial<Record<keyof Filter, unknown>>) : {};
    const strings = (v: unknown) => (Array.isArray(v) ? v.filter((x): x is string => typeof x === 'string') : []);
    return {
      query: typeof stored.query === 'string' ? stored.query : '',
      difficulty: strings(stored.difficulty),
      status: strings(stored.status).filter((s): s is ProblemState => s in STATE_LABEL),
      tags: strings(stored.tags),
      view: stored.view === 'flat' ? 'flat' : 'grouped',
    };
  } catch {
    return EMPTY_FILTER;
  }
}

export function SectionPage({ sectionId, tag, track }: { sectionId: string; tag?: string; track?: string }) {
  const { notify, go, sections } = useApp();
  const section = sections.find((s) => s.id === sectionId);
  const catalogue = useCatalogue();
  const [filter, setFilter] = useState<Filter>(() =>
    tag ? { ...EMPTY_FILTER, tags: [tag] } : storedFilter(sectionId),
  );
  // A tag arriving from a chip replaces the filter (the view switch is kept); derived during render,
  // as React recommends for state that follows a prop, so no effect and no extra paint.
  const [seenTag, setSeenTag] = useState(tag);
  if (tag !== seenTag) {
    setSeenTag(tag);
    if (tag) setFilter({ ...EMPTY_FILTER, view: filter.view, tags: [tag] });
  }
  useEffect(() => {
    sessionStorage.setItem(storageKey(sectionId), JSON.stringify(filter));
  }, [sectionId, filter]);

  if (!section) return <p className="loading">No section named {sectionId}.</p>;
  const data = catalogue && { ...catalogue, problems: catalogue.problems.filter((p) => p.section === sectionId) };
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
                <ProblemCard key={p.id} problem={p} history={data.history} all={catalogue.problems} />
              ))}
            </div>
          ) : (
            <Grouped problems={shown} history={data.history} all={catalogue.problems} openOnly={track} />
          )}
        </div>
      )}
      {data && <Aside {...data} />}
    </main>
  );
}

function Grouped({ problems, history, all, openOnly }: Catalogue & { all: ProblemSummary[]; openOnly?: string }) {
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
                <ProblemCard key={p.id} problem={p} history={history} all={all} />
              ))}
            </div>
          </details>
        );
      })}
    </div>
  );
}

function Aside({ problems, history }: { problems: ProblemSummary[]; history: SessionSummary[] }) {
  const { go } = useApp();
  const next = nextUp(problems, history);
  const s = dashboardStats(problems, history);
  return (
    <aside className="aside">
      {next && (
        <ContinueCard
          eyebrow="Next up"
          title={next.title}
          detail={`${next.difficulty}, ${next.minutes} minutes`}
          action="Start"
          onAction={() => go({ name: 'start', problem: next })}
        />
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
