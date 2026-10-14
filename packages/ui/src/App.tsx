import { useEffect, useMemo, useRef, useState } from 'react';
import type { EngineClient } from '@intervue/local-engine-client';
import type { LanguageEditor } from '@intervue/editor';
import { AppContext, type AppServices, type Screen } from './app-context.ts';
import { Dashboard } from './pages/Dashboard.tsx';
import { DoctorPage } from './pages/DoctorPage.tsx';
import { OverviewPage } from './pages/OverviewPage.tsx';
import { ResultPage } from './pages/ResultPage.tsx';
import { StartScreen } from './pages/StartScreen.tsx';
import type { SectionUi } from './section.ts';

export interface AppProps {
  client: EngineClient;
  section: SectionUi;
  languages: readonly LanguageEditor[];
}

interface Toast {
  text: string;
  tone?: 'good' | 'bad';
}

/** The whole app: one screen at a time, chosen by state rather than URLs (the shell has no address bar). */
export function App({ client, section, languages }: AppProps) {
  const [screen, setScreen] = useState<Screen>({ name: 'loading' });
  const [toast, setToast] = useState<Toast | null>(null);
  const toastTimer = useRef<ReturnType<typeof setTimeout>>(undefined);

  const services = useMemo<AppServices>(
    () => ({
      client,
      languages,
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
    [client, languages],
  );

  // First launch: no stored health results yet, so the Doctor runs before anything else.
  // A second answer (StrictMode runs effects twice) can arrive after the Doctor stored results and
  // would skip it, so only the live effect may choose the screen.
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

  return (
    <AppContext.Provider value={services}>
      <CurrentScreen screen={screen} section={section} />
      {toast && <div className={`toast ${toast.tone ?? ''}`}>{toast.text}</div>}
    </AppContext.Provider>
  );
}

function CurrentScreen({ screen, section }: { screen: Screen; section: SectionUi }) {
  switch (screen.name) {
    case 'loading':
      return <p className="loading">Starting the engine…</p>;
    case 'dashboard':
      return <Dashboard />;
    case 'start':
      return <StartScreen problem={screen.problem} />;
    case 'overview':
      return <OverviewPage session={screen.session} />;
    case 'room':
      return <section.Room key={screen.session.view.id} session={screen.session} />;
    case 'result':
      return <ResultPage session={screen.session} Parts={section.Result} />;
    case 'doctor':
      return <DoctorPage firstRun={screen.firstRun} />;
  }
}
