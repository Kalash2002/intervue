// Hooks the pages share: the problem catalogue with the session history, and the ways into a
// session. Pure page logic stays in lib/; these need the app context.
import { useEffect, useState } from 'react';
import type { ProblemSummary, SessionSummary } from '@intervue/local-engine-client';
import { failed, useApp } from './app-context.ts';
import { timed } from './lib/timed.ts';

export interface Catalogue {
  problems: ProblemSummary[];
  history: SessionSummary[];
}

/** Every problem and every session, loaded once per mount; null until both answer. */
export function useCatalogue(): Catalogue | null {
  const { client, notify } = useApp();
  const [data, setData] = useState<Catalogue | null>(null);
  useEffect(() => {
    Promise.all([client.problems(), client.history()])
      .then(([problems, history]) => setData({ problems, history }))
      .catch(failed(notify));
  }, [client, notify]);
  return data;
}

export function useOpeners() {
  const { client, go, notify } = useApp();
  return {
    open: (id: string) => client.session(id).then((view) => go({ name: 'room', session: timed(view) }), failed(notify)),
    result: (id: string) => client.session(id).then((view) => go({ name: 'result', session: view }), failed(notify)),
  };
}

/** Opens a DSA question's start screen by id; a question not written yet says so instead. */
export function useOpenQuestion() {
  const { client, go, notify } = useApp();
  return (id: string) =>
    client.problems().then((all) => {
      const problem = all.find((p) => p.id === id);
      if (problem) go({ name: 'start', problem });
      else notify('This question arrives in a later batch.');
    }, failed(notify));
}

export function titleOf(problems: readonly ProblemSummary[], id: string): string {
  return problems.find((p) => p.id === id)?.title ?? id;
}
