// The DSA solving room: statement on the left, one editable file with the output below it, and a
// side panel with the assistant (and hints in Practice). It must not show a file tree or phases.
import { useState } from 'react';
import { languageFor } from '@intervue/editor';
import type { SessionView } from '@intervue/local-engine-client';
import { Markdown, TopBar, clockText, timed, useActiveMs, useApp, type SectionRoomProps } from '@intervue/ui';
import {
  AssistantPanel,
  CodeEditor,
  HintControl,
  OutputPane,
  type RoomActions,
  type Workspace,
  Splitter,
  useRoomActions,
  useWorkspace,
} from '@intervue/room';
import './dsa.css';

export function SolveRoom({ session: initial }: SectionRoomProps) {
  const { go } = useApp();
  const [session, setSession] = useState(initial);
  const view = session.view;
  const refresh = (v: SessionView) => setSession(timed(v));
  const ws = useWorkspace(view.id);
  const actions = useRoomActions(view, ws, refresh);
  const activeMs = useActiveMs(session);
  const [target, setTarget] = useState('phase:1');
  const [ending, setEnding] = useState(false);

  return (
    <div className="room">
      <TopBar onHome={() => go({ name: 'dashboard' })}>
        <div className="title">{view.title}</div>
        <div className="spacer" />
        <Clock view={view} activeMs={activeMs} />
        {view.finished ? (
          <button className="primary" type="button" onClick={() => go({ name: 'result', session: view })}>
            Result
          </button>
        ) : (
          <button className="primary" type="button" onClick={() => setEnding(true)}>
            Finish
          </button>
        )}
      </TopBar>
      {ending && (
        <EndDialog onCancel={() => setEnding(false)} onDone={() => (setEnding(false), actions.finish(false))} />
      )}
      <div className="work dsa">
        <section className="statement" aria-label="Statement">
          {/* A one-phase problem has an empty intro: skip blanks so no empty block renders. */}
          {view.brief.map((part, i) => (part ? <Markdown key={i} source={part} /> : null))}
        </section>
        <Splitter size="--files-w" label="Resize the statement" />
        <section className="center">
          <EditorArea ws={ws} finished={view.finished} actions={actions} onRun={() => actions.run(target)} />
          <Splitter size="--out-h" label="Resize the output pane" />
          <OutputPane
            view={view}
            target={target}
            onTarget={setTarget}
            actions={actions}
            runLabel="Run tests"
            testsOnly
          />
        </section>
        <Splitter size="--right-w" label="Resize the side panel" />
        <SidePanel view={view} actions={actions} onChange={refresh} />
      </div>
    </div>
  );
}

function EndDialog({ onCancel, onDone }: { onCancel: () => void; onDone: () => void }) {
  return (
    <div className="modal">
      <div className="modal-box" role="dialog" aria-modal="true" aria-label="End the session?">
        <h3>End the session?</h3>
        <p>The final run scores your code as it is now.</p>
        <div className="modal-actions">
          <button className="ghost" type="button" onClick={onCancel}>
            Cancel
          </button>
          <button className="primary" type="button" onClick={onDone}>
            I&apos;m done
          </button>
        </div>
      </div>
    </div>
  );
}

/** The assistant always; hints only in Practice (PLAN.md 2.8). */
function SidePanel({
  view,
  actions,
  onChange,
}: {
  view: SessionView;
  actions: RoomActions;
  onChange: (v: SessionView) => void;
}) {
  const [tab, setTab] = useState<'Hints' | 'Assistant'>('Assistant');
  const tabs = view.type === 'PRACTICE' ? (['Assistant', 'Hints'] as const) : (['Assistant'] as const);
  return (
    <aside className="right">
      <div className="tabs side" role="tablist">
        {tabs.map((t) => (
          <button
            key={t}
            role="tab"
            aria-selected={tab === t}
            className={tab === t ? 'tab active' : 'tab'}
            type="button"
            onClick={() => setTab(t)}
          >
            {t}
          </button>
        ))}
      </div>
      <div className="side-body" role="tabpanel" aria-label={tab}>
        {tab === 'Assistant' && <AssistantPanel view={view} actions={actions} onChange={onChange} />}
        {tab === 'Hints' && (
          <HintControl
            sessionId={view.id}
            phase={1}
            used={view.hintsUsed['1'] ?? 0}
            disabled={view.finished}
            onRevealed={onChange}
            beforeExplain={actions.saveFiles}
          />
        )}
      </div>
    </aside>
  );
}

function Clock({ view, activeMs }: { view: SessionView; activeMs: number }) {
  const limit = view.minutes * 60_000;
  if (!view.timed) return <div className="clock">{clockText(activeMs)}</div>;
  return activeMs > limit ? (
    <div className="clock over">+{clockText(activeMs - limit)}</div>
  ) : (
    <div className="clock">{clockText(limit - activeMs)}</div>
  );
}

/** The single editable file; a one-file problem needs no tree. */
function EditorArea({
  ws,
  finished,
  actions,
  onRun,
}: {
  ws: Workspace;
  finished: boolean;
  actions: RoomActions;
  onRun: () => void;
}) {
  const { languages } = useApp();
  const path = ws.active;
  const doc = path ? ws.docs[path] : undefined;
  if (!path || !doc) return <div className="editor-wrap" />;
  return (
    <CodeEditor
      key={path}
      path={path}
      text={doc.text}
      readOnly={finished || (ws.files.find((f) => f.path === path)?.readOnly ?? false)}
      language={languageFor(languages, path)}
      symbols={ws.symbols}
      diagnostics={ws.diagnostics}
      marks={ws.marks[path] ?? []}
      onChange={(text) => ws.edit(path, text)}
      onUndo={actions.undo}
      onSave={actions.save}
      onRun={onRun}
    />
  );
}
