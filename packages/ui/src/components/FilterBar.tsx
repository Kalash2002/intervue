// The section page's filter bar: search, difficulty, status and tag chips, the view switch, Random
// unsolved and Reset. Owns only the "More" toggle: it renders a Filter and reports changes.
import { useState, type ReactNode } from 'react';
import { EMPTY_FILTER, STATE_LABEL, type Filter, type ProblemState } from '../lib/progress.ts';
import { Chip } from './Chip.tsx';

type Kind = 'difficulty' | 'status' | 'tags';
type Option<K extends Kind> = { value: Filter[K][number]; label: ReactNode };

const DIFFICULTIES: Option<'difficulty'>[] = ['Easy', 'Medium', 'Hard'].map((d) => ({ value: d, label: d }));
const STATUSES: Option<'status'>[] = (Object.keys(STATE_LABEL) as ProblemState[]).map((value) => ({
  value,
  label: STATE_LABEL[value].label,
}));
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
  const tagOptions: Option<'tags'>[] = (more ? tags : tags.slice(0, SHOWN_TAGS)).map(({ tag, count }) => ({
    value: tag,
    label: (
      <>
        {tag}
        <span className="count">{count}</span>
      </>
    ),
  }));
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
        <ChipGroup kind="difficulty" label="Difficulty" options={DIFFICULTIES} filter={filter} onChange={onChange} />
        <ChipGroup kind="status" label="Status" options={STATUSES} filter={filter} onChange={onChange} />
        <ViewActions filter={filter} onChange={onChange} onRandom={onRandom} />
      </div>
      {tags.length > 0 && (
        <div className="chips" role="group" aria-label="Filter by tag">
          <ChipGroup kind="tags" options={tagOptions} filter={filter} onChange={onChange} />
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

/** One kind of filter as toggle chips; OR within the kind, so every chip is independent. */
function ChipGroup<K extends Kind>({
  kind,
  label,
  options,
  filter,
  onChange,
}: {
  kind: K;
  /** Names the group for assistive tech; the tag group is named by its parent. */
  label?: string;
  options: Option<K>[];
  filter: Filter;
  onChange: (next: Filter) => void;
}) {
  const chosen = filter[kind] as readonly Filter[K][number][];
  return (
    <div className="chips" role={label ? 'group' : undefined} aria-label={label}>
      {options.map((o) => (
        <Chip
          key={String(o.value)}
          on={chosen.includes(o.value)}
          onClick={() => onChange({ ...filter, [kind]: toggled(chosen, o.value) })}
        >
          {o.label}
        </Chip>
      ))}
    </div>
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
