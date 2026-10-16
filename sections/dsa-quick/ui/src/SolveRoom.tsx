// The DSA solving room: statement on the left, one editable file with the output below it, and a
// side panel with hints and, where the section allows it, the assistant. It must not show a file tree or phases.
import { useState } from 'react';
import { languageFor } from '@intervue/editor';
import type { FileEntry, SessionView } from '@intervue/local-engine-client';
import { TopBar, timed, useActiveMs, useApp, type SectionRoomProps } from '@intervue/ui';
import {
  AssistantPanel,
  CodeEditor,
  EndDialog,
  HintControl,
  OutputPane,
  phaseTargets,
  RoomClock,
  type RoomActions,
  type Workspace,
  Splitter,
  useRoomActions,
  useWorkspace,
} from '@intervue/room';
import { Statement } from './Statement.tsx';
import { solutionFiles } from './solutionFiles.ts';
import './dsa.css';

const pickSolution = (files: readonly FileEntry[]) => solutionFiles(files)[0] ?? null;

export function SolveRoom({ session: initial }: SectionRoomProps) {
  const { go } = useApp();
  const [session, setSession] = useState(initial);
  const view = session.view;
  const refresh = (v: SessionView) => setSession(timed(v));
  const ws = useWorkspace(view.id, pickSolution);
  const actions = useRoomActions(view, ws, refresh);
  const activeMs = useActiveMs(session);
  const [target, setTarget] = useState('phase:1');
  const hasSide = view.hintsAllowed || view.channels.length > 0;

  return (
    <div className="room dsa">
      <TopBar onHome={() => go({ name: 'dashboard' })}>
        <div className="title">{view.title}</div>
        <div className="spacer" />
        <RoomClock view={view} activeMs={activeMs} />
        <FinishControl view={view} actions={actions} />
      </TopBar>
      <div className={hasSide ? 'work dsa' : 'work dsa solo'}>
        <section className="statement" aria-label="Statement">
          {/* A one-phase problem has an empty intro: skip blanks so no empty block renders. */}
          {view.brief.map((part, i) => (part ? <Statement key={i} source={part} /> : null))}
        </section>
        <Splitter size="--statement-w" label="Resize the statement" />
        <section className="center">
          <EditorArea ws={ws} finished={view.finished} actions={actions} onRun={() => actions.run(target)} />
          <Splitter size="--out-h" label="Resize the output pane" />
          <OutputPane
            view={view}
            target={target}
            onTarget={setTarget}
            actions={actions}
            runLabel="Run tests"
            targets={phaseTargets(view)}
          />
        </section>
        {hasSide && (
          <>
            <Splitter size="--right-w" label="Resize the side panel" />
            <SidePanel view={view} actions={actions} onChange={refresh} />
          </>
        )}
      </div>
    </div>
  );
}

/** Result once finished, otherwise Finish with its confirmation dialog. */
function FinishControl({ view, actions }: { view: SessionView; actions: RoomActions }) {
  const { go } = useApp();
  const [ending, setEnding] = useState(false);
  if (view.finished) {
    return (
      <button className="primary" type="button" onClick={() => go({ name: 'result', session: view })}>
        Result
      </button>
    );
  }
  return (
    <>
      <button className="primary" type="button" onClick={() => setEnding(true)}>
        Finish
      </button>
      {ending && (
        <EndDialog
          onCancel={() => setEnding(false)}
          onDone={() => {
            setEnding(false);
            actions.finish(false);
          }}
        >
          The final run scores your code as it is now.
        </EndDialog>
      )}
    </>
  );
}

type Tab = 'Hints' | 'Assistant';

/** Hints when the session allows them, the assistant only when it has a channel (ADR 0012). */
function SidePanel({
  view,
  actions,
  onChange,
}: {
  view: SessionView;
  actions: RoomActions;
  onChange: (v: SessionView) => void;
}) {
  const tabs: Tab[] = [];
  if (view.channels.length > 0) tabs.push('Assistant');
  if (view.hintsAllowed) tabs.push('Hints');
  const [picked, setTab] = useState<Tab | null>(null);
  const tab = picked && tabs.includes(picked) ? picked : tabs[0];
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
            channels={view.channels}
            onRevealed={onChange}
            beforeExplain={actions.saveFiles}
          />
        )}
      </div>
    </aside>
  );
}

/** The solution file; tabs appear only when several files qualify. */
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
  const candidates = solutionFiles(ws.files);
  return (
    <>
      {candidates.length > 1 && (
        <div className="tabs" role="tablist" aria-label="Solution files">
          {candidates.map((p) => (
            <button
              key={p}
              role="tab"
              aria-selected={p === path}
              className={p === path ? 'tab active' : 'tab'}
              type="button"
              onClick={() => void ws.open(p)}
            >
              {p}
            </button>
          ))}
        </div>
      )}
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
    </>
  );
}
