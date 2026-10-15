import { useEffect, useMemo, useRef, useState } from 'react';
import type { EngineClient } from '@intervue/local-engine-client';
import type { LanguageEditor } from '@intervue/editor';
import { AppContext, useApp, type AppServices, type Screen } from './app-context.ts';
import { Shell } from './components/Shell.tsx';
import { applyTheme, storedTheme } from './components/TopBar.tsx';
import { AnswerPage } from './pages/AnswerPage.tsx';
import { DoctorPage } from './pages/DoctorPage.tsx';
import { HomePage } from './pages/HomePage.tsx';
import { OverviewPage } from './pages/OverviewPage.tsx';
import { ResultPage } from './pages/ResultPage.tsx';
import { SectionPage } from './pages/SectionPage.tsx';
import { StartScreen } from './pages/StartScreen.tsx';
import type { SectionUi } from './section.ts';

export interface AppProps {
  client: EngineClient;
  /** Every section UI the build ships, in menu order (the registry by section id). */
  sections: readonly SectionUi[];
  languages: readonly LanguageEditor[];
}

interface Toast {
  text: string;
  tone?: 'good' | 'bad';
}

/** The whole app: one screen at a time, chosen by state rather than URLs (the shell has no address bar). */
export function App({ client, sections, languages }: AppProps) {
  const [screen, setScreen] = useState<Screen>({ name: 'loading' });
  const [toast, setToast] = useState<Toast | null>(null);
  const toastTimer = useRef<ReturnType<typeof setTimeout>>(undefined);

  const services = useMemo<AppServices>(
    () => ({
      client,
      languages,
      sections,
      go: (next) => {
        setScreen(next);
        window.scrollTo?.(0, 0);
      },
      notify: (text, tone) => {
        setToast({ text, tone });
        clearTimeout(toastTimer.current);
        toastTimer.current = setTimeout(() => setToast(null), 5000);
      },
    }),
    [client, languages, sections],
  );

  // First launch: no stored health results yet, so the Doctor runs before anything else.
  // A second answer (StrictMode runs effects twice) can arrive after the Doctor stored results and
  // would skip it, so only the live effect may choose the screen.
  useEffect(() => applyTheme(storedTheme()), []);

  useEffect(() => {
    let live = true;
    client
      .health()
      .then((results) => {
        if (live) setScreen(results.length ? { name: 'dashboard' } : { name: 'doctor', firstRun: true });
      })
      .catch((error: Error) => setToast({ text: `The engine is not answering: ${error.message}`, tone: 'bad' }));
    return () => {
      live = false;
    };
  }, [client]);

  // The room takes the whole window; every other screen sits beside the menu.
  const body =
    screen.name === 'loading' ? <p className="loading">Starting the engine…</p> : <CurrentScreen screen={screen} />;
  return (
    <AppContext.Provider value={services}>
      {screen.name === 'room' || screen.name === 'loading' ? body : <Shell screen={screen}>{body}</Shell>}
      {toast && <div className={`toast ${toast.tone ?? ''}`}>{toast.text}</div>}
    </AppContext.Provider>
  );
}

function CurrentScreen({ screen }: { screen: Exclude<Screen, { name: 'loading' }> }) {
  switch (screen.name) {
    case 'dashboard':
      return <HomePage />;
    case 'section':
      return <SectionPage key={screen.sectionId} sectionId={screen.sectionId} tag={screen.tag} track={screen.track} />;
    case 'start':
      return <StartScreen problem={screen.problem} />;
    case 'overview':
      return <OverviewPage session={screen.session} />;
    case 'room':
    case 'result':
      return <SectionScreen screen={screen} />;
    case 'answer':
      return <AnswerPage problem={screen.problem} />;
    case 'doctor':
      return <DoctorPage firstRun={screen.firstRun} />;
  }
}

/** The two screens a section package draws: its room and its result parts. */
function SectionScreen({ screen }: { screen: Extract<Screen, { name: 'room' | 'result' }> }) {
  const { sections } = useApp();
  const id = screen.name === 'room' ? screen.session.view.section : screen.session.section;
  const section = sections.find((s) => s.id === id);
  if (!section) return <p className="loading">No section UI for {id}.</p>;
  if (screen.name === 'room') return <section.Room key={screen.session.view.id} session={screen.session} />;
  return <ResultPage session={screen.session} Parts={section.Result} />;
}
