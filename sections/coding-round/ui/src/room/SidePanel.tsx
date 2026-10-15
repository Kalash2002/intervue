import { useState } from 'react';
import type { SessionView } from '@intervue/local-engine-client';
import { Markdown } from '@intervue/ui';
import { AssistantPanel, HintControl, type RoomActions } from '@intervue/room';
import { GuidePanel } from './GuidePanel.tsx';

type Tab = 'Guide' | 'Brief' | 'Hints' | 'Assistant';

export function SidePanel({
  view,
  actions,
  onChange,
}: {
  view: SessionView;
  actions: RoomActions;
  onChange: (v: SessionView) => void;
}) {
  const [tab, setTab] = useState<Tab>('Guide');
  // Kept here, not in the Guide tab: the tab unmounts on every switch.
  const [started, setStarted] = useState<ReadonlySet<number>>(new Set());
  // Hints exist only in Practice (PLAN.md 2.8); an Interview room never offers the tab.
  const tabs: Tab[] =
    view.type === 'PRACTICE' ? ['Guide', 'Brief', 'Hints', 'Assistant'] : ['Guide', 'Brief', 'Assistant'];
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
        {tab === 'Guide' && (
          <GuidePanel
            view={view}
            actions={actions}
            started={started}
            onStart={(phase) => setStarted(new Set(started).add(phase))}
          />
        )}
        {tab === 'Brief' && <Brief view={view} onSkip={actions.skip} />}
        {tab === 'Hints' && <Hints view={view} onChange={onChange} beforeExplain={actions.saveFiles} />}
        {tab === 'Assistant' && <AssistantPanel view={view} actions={actions} onChange={onChange} />}
      </div>
    </aside>
  );
}

function Brief({ view, onSkip }: { view: SessionView; onSkip: () => void }) {
  const next = view.unlocked + 1;
  return (
    <>
      {view.brief.map((part, i) => part !== null && <Markdown key={i} source={part} />)}
      {next <= view.phases && (
        <div className="locked-phase">
          Phase {next} opens when the phase {view.unlocked} tests pass.
          {!view.finished && (
            <>
              <br />
              <button className="ghost small" type="button" onClick={onSkip}>
                Stuck? Open phase {next} anyway
              </button>
            </>
          )}
        </div>
      )}
    </>
  );
}

function Hints({
  view,
  onChange,
  beforeExplain,
}: {
  view: SessionView;
  onChange: (v: SessionView) => void;
  beforeExplain: () => Promise<void>;
}) {
  const phases = Array.from({ length: view.unlocked }, (_, i) => i + 1);
  return (
    <>
      <p className="hint">Each phase has three levels: where to look, the approach, then concrete steps.</p>
      {phases.map((phase) => (
        <section key={phase}>
          <h3>Phase {phase}</h3>
          <HintControl
            sessionId={view.id}
            phase={phase}
            used={view.hintsUsed[String(phase)] ?? 0}
            disabled={view.finished}
            onRevealed={onChange}
            beforeExplain={beforeExplain}
          />
        </section>
      ))}
    </>
  );
}
