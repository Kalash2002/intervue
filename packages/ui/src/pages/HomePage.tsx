// Home: a greeting with the streak in the aside, what to continue, one track card per section and
// the last finished sessions. Everything comes from the problem list and the history; the stat
// tiles live on the section page.
import type { SessionSummary } from '@intervue/local-engine-client';
import { useApp } from '../app-context.ts';
import { ContinueCard } from '../components/ContinueCard.tsx';
import { PlannedTrackCard, TrackCard } from '../components/TrackCard.tsx';
import { titleOf, useCatalogue, useOpeners, type Catalogue } from '../hooks.ts';
import { dateText, plural } from '../lib/format.ts';
import { nextUp } from '../lib/progress.ts';
import { streak, weekStrip } from '../lib/streak.ts';

/** Sections the roadmap promises but no package ships yet; Home shows them as placeholders. */
const PLANNED = [{ id: 'dsa-quick', title: 'DSA' }];
const RECENT = 5;

export function HomePage() {
  const data = useCatalogue();
  return (
    <main className="page with-aside home-wash">
      <header className="page-head">
        <div>
          <h1>{greeting()}</h1>
          <p className="lede">Interview sessions count towards solved problems. Practice sessions are for learning.</p>
        </div>
      </header>
      {data && (
        <div className="section-grid">
          <Continue {...data} />
          <Tracks {...data} />
          <Recent {...data} />
        </div>
      )}
      {data && <StreakCard history={data.history} />}
    </main>
  );
}

function greeting(): string {
  const h = new Date().getHours();
  return h < 12 ? 'Good morning.' : h < 18 ? 'Good afternoon.' : 'Good evening.';
}

function Continue({ problems, history }: Catalogue) {
  const { go, sections } = useApp();
  const { open } = useOpeners();
  const openSessions = history.filter((s) => !s.finished).slice(0, 3);
  const next = sections.flatMap((section) => {
    const problem = nextUp(
      problems.filter((p) => p.section === section.id),
      history,
    );
    return problem ? [{ section, problem }] : [];
  });
  return (
    <section aria-label="Continue">
      <h2>Continue</h2>
      <div className="cards">
        {openSessions.length > 0 ? (
          openSessions.map((s) => (
            <ContinueCard
              key={s.id}
              eyebrow={s.type === 'PRACTICE' ? 'Practice in progress' : 'Interview in progress'}
              title={titleOf(problems, s.problemId)}
              detail={`Started ${dateText(s.startedAt)}`}
              action="Resume"
              onAction={() => open(s.id)}
            />
          ))
        ) : next.length > 0 ? (
          next.map(({ section, problem }) => (
            <ContinueCard
              key={section.id}
              eyebrow={section.title}
              title={
                <>
                  Next: <span>{problem.title}</span>
                </>
              }
              detail={`${problem.difficulty}, ${problem.minutes} minutes`}
              action="Start"
              onAction={() => go({ name: 'start', problem })}
            />
          ))
        ) : (
          <p className="hint">Every problem is solved. Well done.</p>
        )}
      </div>
    </section>
  );
}

function Tracks({ problems, history }: Catalogue) {
  const { go, sections } = useApp();
  return (
    <section aria-label="Tracks">
      <h2>Tracks</h2>
      <div className="cards tracks">
        {sections.map((s) => (
          <TrackCard
            key={s.id}
            title={s.title}
            problems={problems.filter((p) => p.section === s.id)}
            history={history}
            onOpen={() => go({ name: 'section', sectionId: s.id })}
            onCell={(track) => go({ name: 'section', sectionId: s.id, track })}
          />
        ))}
        {PLANNED.filter((p) => !sections.some((s) => s.id === p.id)).map((p) => (
          <PlannedTrackCard key={p.id} title={p.title} />
        ))}
      </div>
    </section>
  );
}

function Recent({ problems, history }: Catalogue) {
  const { go } = useApp();
  const { result } = useOpeners();
  const rows = history
    .filter((s) => s.finished)
    .sort((a, b) => b.startedAt - a.startedAt)
    .slice(0, RECENT);
  return (
    <section aria-label="Recent results">
      <h2>Recent results</h2>
      <div className="rows">
        {rows.length === 0 && <div className="empty-row">No finished sessions yet.</div>}
        {rows.map((s) => {
          const problem = problems.find((p) => p.id === s.problemId);
          return (
            <div className="row" key={s.id}>
              <div>
                <h3>
                  {problem?.title ?? s.problemId}
                  <span className="level">{s.total} / 100</span>
                </h3>
                <p className="meta">
                  <span className={`label ${s.type.toLowerCase()}`}>{s.label ?? s.type}</span> {dateText(s.startedAt)}
                </p>
              </div>
              <div className="actions">
                <button className="ghost" type="button" onClick={() => result(s.id)}>
                  Result
                </button>
                {problem && (
                  <button className="ghost" type="button" onClick={() => go({ name: 'answer', problem })}>
                    Answer
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

function StreakCard({ history }: { history: SessionSummary[] }) {
  const { current, best } = streak(history);
  const days = weekStrip(history);
  return (
    <aside className="aside">
      <div className="card" aria-label="Streak">
        <p className="eyebrow">Streak</p>
        <p className="streak">
          <b>{plural(current, 'day')}</b>
          <span className="hint">best {best}</span>
        </p>
        <ol className="week" aria-label="Last seven days">
          {days.map((d, i) => (
            <li key={i} className={d.done ? 'done' : ''} title={d.done ? `${d.label}: a session finished` : d.label}>
              <span className="dot" aria-hidden="true" />
              {d.label}
            </li>
          ))}
        </ol>
      </div>
    </aside>
  );
}
