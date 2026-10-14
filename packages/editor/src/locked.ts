// Ranges the candidate cannot type into. Whole read-only files use EditorState.readOnly instead;
// this is for parts of a file. Engine-applied edits dispatch with `filter: false` and pass through.
import { EditorState, StateEffect, StateField } from '@codemirror/state';

export interface Range {
  from: number;
  to: number;
}

export const setLockedRanges = StateEffect.define<Range[]>();

const lockedField = StateField.define<Range[]>({
  create: () => [],
  update(ranges, tr) {
    for (const effect of tr.effects) if (effect.is(setLockedRanges)) return effect.value;
    if (!tr.docChanged) return ranges;
    return ranges.map((r) => ({ from: tr.changes.mapPos(r.from, 1), to: tr.changes.mapPos(r.to, -1) }));
  },
});

// The filter returns the blocked ranges as a flat [from, to, ...] list: true would allow everything.
const lockFilter = EditorState.changeFilter.of((tr) => {
  const ranges = tr.startState.field(lockedField);
  return ranges.length === 0 ? true : ranges.flatMap((r) => [r.from, r.to]);
});

export const lockedRanges = [lockedField, lockFilter];
