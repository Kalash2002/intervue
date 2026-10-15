import { ConfirmButton } from '@intervue/ui';
import type { Workspace } from '@intervue/room';

export function FileTree({
  ws,
  finished,
  onResetFile,
  onResetProblem,
}: {
  ws: Workspace;
  finished: boolean;
  onResetFile: (path: string) => void;
  onResetProblem: () => void;
}) {
  const active = ws.active;
  return (
    <aside className="files">
      <div className="pane-head">
        Files
        {!finished && active && (
          <ConfirmButton className="icon" label="↺" confirmLabel="Reset file?" onConfirm={() => onResetFile(active)} />
        )}
      </div>
      <ul>
        {ws.files.map((f) => {
          const doc = ws.docs[f.path];
          const dirty = doc && doc.text !== doc.saved;
          return (
            <li key={f.path}>
              <button
                type="button"
                className={f.path === active ? 'active' : ''}
                onClick={() => ws.open(f.path)}
                title={f.readOnly ? 'Read-only' : f.path}
              >
                {dirty && <span className="dirty" aria-label="unsaved" />}
                {f.path}
                {f.readOnly && <span className="lock">read-only</span>}
              </button>
            </li>
          );
        })}
      </ul>
      {!finished && (
        <p className="hint" style={{ padding: '0 12px' }}>
          <ConfirmButton
            className="ghost small"
            label="Reset problem"
            confirmLabel="Restore every file?"
            onConfirm={onResetProblem}
          />
        </p>
      )}
    </aside>
  );
}
