// The section page's filter bar: search, difficulty, status and tag chips, the view switch, Random
// unsolved and Reset. Owns no state: it renders a Filter and reports changes.
import { useState } from 'react';
import { EMPTY_FILTER, type Filter, type ProblemState } from '../lib/progress.ts';

const DIFFICULTIES = ['Easy', 'Medium', 'Hard'];
const STATUSES: [ProblemState, string][] = [
  ['solved', 'Solved'],
  ['progress', 'In progress'],
  ['attempted', 'Attempted'],
  ['new', 'Not started'],
];
const SHOWN_TAGS = 10;

function toggled<T>(list: readonly T[], item: T): T[] {
  return list.includes(item) ? list.filter((x) => x !== item) : [...list, item];
}

export function FilterBar({
  filter,
  tags,
  onChange,
  onRandom,
}: {
  filter: Filter;
  tags: readonly { tag: string; count: number }[];
  onChange: (next: Filter) => void;
  onRandom: () => void;
}) {
  const [more, setMore] = useState(false);
  const shown = more ? tags : tags.slice(0, SHOWN_TAGS);
  return (
    <div className="filter-bar">
      <div className="filter-row">
        <input
          type="search"
          className="search"
          aria-label="Search problems"
          placeholder="Search problems"
          value={filter.query}
          onChange={(e) => onChange({ ...filter, query: e.target.value })}
        />
        <Kinds filter={filter} onChange={onChange} />
        <ViewActions filter={filter} onChange={onChange} onRandom={onRandom} />
      </div>
      {tags.length > 0 && (
        <div className="chips" role="group" aria-label="Filter by tag">
          {shown.map(({ tag, count }) => (
            <Chip
              key={tag}
              on={filter.tags.includes(tag)}
              onClick={() => onChange({ ...filter, tags: toggled(filter.tags, tag) })}
            >
              {tag}
              <span className="count">{count}</span>
            </Chip>
          ))}
          {tags.length > SHOWN_TAGS && (
            <button className="ghost small" type="button" onClick={() => setMore(!more)}>
              {more ? 'Fewer' : `More (${tags.length - SHOWN_TAGS})`}
            </button>
          )}
        </div>
      )}
    </div>
  );
}

function Kinds({ filter, onChange }: { filter: Filter; onChange: (next: Filter) => void }) {
  return (
    <>
      <div className="chips" role="group" aria-label="Difficulty">
        {DIFFICULTIES.map((d) => (
          <Chip
            key={d}
            on={filter.difficulty.includes(d)}
            onClick={() => onChange({ ...filter, difficulty: toggled(filter.difficulty, d) })}
          >
            {d}
          </Chip>
        ))}
      </div>
      <div className="chips" role="group" aria-label="Status">
        {STATUSES.map(([state, label]) => (
          <Chip
            key={state}
            on={filter.status.includes(state)}
            onClick={() => onChange({ ...filter, status: toggled(filter.status, state) })}
          >
            {label}
          </Chip>
        ))}
      </div>
    </>
  );
}

function ViewActions({
  filter,
  onChange,
  onRandom,
}: {
  filter: Filter;
  onChange: (next: Filter) => void;
  onRandom: () => void;
}) {
  return (
    <div className="filter-actions">
      <div className="chips" role="group" aria-label="View">
        <Chip on={filter.view === 'grouped'} onClick={() => onChange({ ...filter, view: 'grouped' })}>
          Grouped
        </Chip>
        <Chip on={filter.view === 'flat'} onClick={() => onChange({ ...filter, view: 'flat' })}>
          Flat
        </Chip>
      </div>
      <button className="ghost small" type="button" onClick={onRandom}>
        Random unsolved
      </button>
      <button className="ghost small" type="button" onClick={() => onChange({ ...EMPTY_FILTER, view: filter.view })}>
        Reset
      </button>
    </div>
  );
}

function Chip({ on, onClick, children }: { on: boolean; onClick: () => void; children: React.ReactNode }) {
  return (
    <button type="button" className={on ? 'chip on' : 'chip'} aria-pressed={on} onClick={onClick}>
      {children}
    </button>
  );
}
