// Shared fixtures for the app tests: a fake engine client, one section UI, and summaries.
import { render } from '@testing-library/react';
import type {
  CheckResult,
  EngineClient,
  ProblemSummary,
  SessionSummary,
  SessionView,
} from '@intervue/local-engine-client';
import { vi } from 'vitest';
import { App, type SectionUi } from '../src/index.ts';

export const problem = (id: string, title: string, over: Partial<ProblemSummary> = {}): ProblemSummary => ({
  id,
  section: 'coding-round',
  title,
  summary: `${title} summary`,
  difficulty: 'Medium',
  techniques: ['graphs'],
  tags: [],
  minutes: 60,
  phases: 3,
  free: false,
  ...over,
});

export const summary = (over: Partial<SessionSummary>): SessionSummary => ({
  id: 's',
  problemId: 'hold-exposure',
  type: 'INTERVIEW',
  startedAt: 0,
  finished: true,
  total: 70,
  label: 'Interview',
  solved: true,
  countsAsSolved: true,
  ...over,
});

export const check = (over: Partial<CheckResult>): CheckResult => ({
  id: 'os',
  title: 'Operating system',
  blocking: true,
  status: 'PASS',
  message: 'Windows 11',
  measured: null,
  ranAt: 0,
  ...over,
});

export const session = {
  id: 'new-1',
  problemId: 'hold-exposure',
  section: 'coding-round',
  type: 'PRACTICE',
  minutes: 60,
  phases: 3,
  phaseTitles: ['Fix the bug', 'Peak and breach', 'Busy books'],
  tags: [],
} as unknown as SessionView;

export function fakeClient(over: Partial<EngineClient> = {}): EngineClient {
  return {
    health: vi.fn(async () => [check({})]),
    runHealth: vi.fn(async () => ({ ready: true, results: [check({})] })),
    problems: vi.fn(async () => [
      problem('hold-exposure', 'Hold Exposure'),
      problem('corridor-router', 'Corridor Router'),
    ]),
    history: vi.fn(async () => []),
    createSession: vi.fn(async () => session),
    ...over,
  } as unknown as EngineClient;
}

export const section: SectionUi = {
  id: 'coding-round',
  title: 'AI-assisted coding',
  description: 'Three-phase scenarios with an assistant.',
  Room: ({ session: s }) => <p>room for {s.view.id}</p>,
  Result: () => <p>result parts</p>,
};

export const renderApp = (client: EngineClient) => render(<App client={client} sections={[section]} languages={[]} />);
