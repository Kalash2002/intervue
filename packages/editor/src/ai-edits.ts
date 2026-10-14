// Decorations for edits the engine applied from a Write code reply (PLAN.md 2.8): the changed lines
// are highlighted and carry an Undo button that hands the edit id back to the caller.
import { StateEffect, StateField, type EditorState, type Extension, type Range } from '@codemirror/state';
import { Decoration, EditorView, WidgetType, type DecorationSet } from '@codemirror/view';

export interface AiEditMark {
  editId: string;
  /** 1-based, inclusive. */
  fromLine: number;
  toLine: number;
}

/** The first and last changed line of `after` against `before`, 1-based; null when equal. */
export function changedRange(before: string, after: string): { fromLine: number; toLine: number } | null {
  if (before === after) return null;
  const a = before.split('\n');
  const b = after.split('\n');
  let start = 0;
  while (start < a.length && start < b.length && a[start] === b[start]) start++;
  let endA = a.length - 1;
  let endB = b.length - 1;
  while (endA >= start && endB >= start && a[endA] === b[endB]) {
    endA--;
    endB--;
  }
  // ponytail: one block from the first to the last changed line; per-hunk marks if users ask.
  return { fromLine: start + 1, toLine: Math.max(start, endB) + 1 };
}

const addMark = StateEffect.define<AiEditMark>();
/** Removes every AI-edit mark; the room re-adds the ones still active. */
export const clearAiEdits = StateEffect.define<null>();

class UndoWidget extends WidgetType {
  constructor(
    readonly editId: string,
    readonly onUndo: (editId: string) => void,
  ) {
    super();
  }

  override eq(other: UndoWidget) {
    return other.editId === this.editId;
  }

  toDOM() {
    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'cm-ai-undo';
    button.textContent = 'Undo';
    button.title = 'Undo this AI edit';
    button.onclick = () => this.onUndo(this.editId);
    return button;
  }
}

function decorationsFor(state: EditorState, mark: AiEditMark, onUndo: (id: string) => void) {
  const doc = state.doc;
  const ranges: Range<Decoration>[] = [];
  const last = Math.min(mark.toLine, doc.lines);
  for (let n = Math.max(1, mark.fromLine); n <= last; n++) {
    ranges.push(Decoration.line({ class: 'cm-ai-edit' }).range(doc.line(n).from));
  }
  const widget = Decoration.widget({ widget: new UndoWidget(mark.editId, onUndo), side: 1 });
  ranges.push(widget.range(doc.line(Math.max(1, last)).to));
  return ranges;
}

/** The field and theme; `onUndo` receives the edit id when its Undo button is pressed. */
export function aiEdits(onUndo: (editId: string) => void): Extension {
  const field = StateField.define<DecorationSet>({
    create: () => Decoration.none,
    update(marks, tr) {
      marks = marks.map(tr.changes);
      for (const effect of tr.effects) {
        if (effect.is(addMark))
          marks = marks.update({ add: decorationsFor(tr.state, effect.value, onUndo), sort: true });
        if (effect.is(clearAiEdits)) marks = Decoration.none;
      }
      return marks;
    },
    provide: (f) => EditorView.decorations.from(f),
  });
  return [field, theme];
}

export function markAiEdit(view: EditorView, mark: AiEditMark): void {
  view.dispatch({ effects: addMark.of(mark) });
}

const theme = EditorView.baseTheme({
  '.cm-ai-edit': { animation: 'cm-ai-fade 6s ease-out forwards' },
  '@keyframes cm-ai-fade': {
    from: { backgroundColor: 'rgba(120, 160, 255, 0.28)' },
    to: { backgroundColor: 'rgba(120, 160, 255, 0.08)' },
  },
  '.cm-ai-undo': { marginLeft: '1em', fontSize: '0.8em', cursor: 'pointer' },
});
