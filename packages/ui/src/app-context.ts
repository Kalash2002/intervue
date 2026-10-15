import { createContext, useContext } from 'react';
import type { EngineClient, ProblemSummary, SessionView } from '@intervue/local-engine-client';
import type { LanguageEditor } from '@intervue/editor';
import type { TimedSession } from './lib/timed.ts';

export type Screen =
  | { name: 'loading' }
  | { name: 'dashboard' }
  | { name: 'start'; problem: ProblemSummary }
  | { name: 'overview'; session: TimedSession }
  | { name: 'room'; session: TimedSession }
  | { name: 'result'; session: SessionView }
  | { name: 'answer'; problemId: string; title: string }
  | { name: 'doctor'; firstRun: boolean };

export interface AppServices {
  client: EngineClient;
  languages: readonly LanguageEditor[];
  go: (screen: Screen) => void;
  /** Shows a short message; `bad` for errors. */
  notify: (text: string, tone?: 'good' | 'bad') => void;
}

export const AppContext = createContext<AppServices | null>(null);

export function useApp(): AppServices {
  const services = useContext(AppContext);
  if (!services) throw new Error('useApp outside <App>');
  return services;
}

/** The text to show for a failed call; the engine's errors already carry a mapped reason. */
export function errorText(error: unknown): string {
  return error instanceof Error ? error.message : String(error);
}

/** Reports a failed engine call through the toast instead of an unhandled rejection. */
export function failed(notify: AppServices['notify']) {
  return (error: unknown) => notify(errorText(error), 'bad');
}
