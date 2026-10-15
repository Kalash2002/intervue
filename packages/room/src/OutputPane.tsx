import type { SessionView } from '@intervue/local-engine-client';
import type { RoomActions } from './useRoomActions.ts';

/** Colours the kit's human-readable lines as the prototype did; results themselves come from the protocol. */
function lineClass(line: string): string {
  if (/^PASS /.test(line)) return 'pass';
  if (/^(FAIL |TIMEOUT |Compilation failed|Stopped:)/.test(line) || /error:/.test(line)) return 'fail';
  return '';
}

type Target = { value: string; label: string };

/** One "Phase N tests" target per phase. */
export function phaseTargets(view: SessionView): Target[] {
  return Array.from({ length: view.phases }, (_, i) => ({
    value: `phase:${i + 1}`,
    label: `Phase ${i + 1} tests`,
  }));
}

export function targets(view: SessionView): Target[] {
  return [...phaseTargets(view), { value: 'all', label: 'All tests' }, { value: 'main', label: 'Main (demo)' }];
}

export function OutputPane({
  view,
  target,
  onTarget,
  actions,
  runLabel = 'Run',
  targets: options = targets(view),
}: {
  view: SessionView;
  target: string;
  onTarget: (t: string) => void;
  actions: RoomActions;
  /** The run button's text. */
  runLabel?: string;
  /** What the select offers; one target hides it. Defaults to the coding round's phases, all and main. */
  targets?: Target[];
}) {
  return (
    <div className="output">
      <div className="pane-head">
        Output
        <div className="run-controls">
          <TargetSelect options={options} target={target} onTarget={onTarget} />
          <button
            className="primary small"
            type="button"
            disabled={actions.busy || view.finished}
            title="Save and run (Ctrl+Enter)"
            onClick={() => actions.run(target)}
          >
            {actions.busy ? 'Running' : runLabel}
          </button>
          <button className="ghost small" type="button" onClick={actions.clearOutput}>
            Clear
          </button>
        </div>
      </div>
      <pre className="output-text" tabIndex={0}>
        {actions.output.length === 0 && (
          <span className="dim">Nothing has run yet. Ctrl+S saves; Ctrl+Enter saves and runs.</span>
        )}
        {actions.output.map((block, i) => (
          <span key={i}>
            <span className="head">── {block.header} ──</span>
            {block.text.split('\n').map((line, j) => (
              <span key={j} className={lineClass(line)}>
                {line + '\n'}
              </span>
            ))}
          </span>
        ))}
      </pre>
    </div>
  );
}

/** Hidden when only one thing can run. */
function TargetSelect({
  options,
  target,
  onTarget,
}: {
  options: Target[];
  target: string;
  onTarget: (t: string) => void;
}) {
  if (options.length < 2) return null;
  return (
    <select aria-label="What to run" value={target} onChange={(e) => onTarget(e.target.value)}>
      {options.map((t) => (
        <option key={t.value} value={t.value}>
          {t.label}
        </option>
      ))}
    </select>
  );
}
