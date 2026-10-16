// Test helpers for the DSA section: a one-phase session view and a render with a mocked engine client.
import { render } from '@testing-library/react';
import type { ReactNode } from 'react';
import type { EngineClient, SessionView } from '@intervue/local-engine-client';
import { AppContext, type AppServices } from '@intervue/ui';
import { javaEditor } from '@intervue/lang-java-editor';
import { vi } from 'vitest';

export function view(over: Partial<SessionView> = {}): SessionView {
  return {
    id: 's1',
    problemId: 'two-sum',
    section: 'dsa-quick',
    title: 'Two Sum',
    type: 'INTERVIEW',
    minutes: 20,
    timed: true,
    activeMs: 0,
    finished: false,
    assisted: false,
    unlocked: 1,
    phases: 1,
    phaseTitles: ['Solve'],
    tags: [],
    phasesPassed: {},
    hintsUsed: {},
    aiEditsActive: 0,
    aiEditsApplied: 0,
    aiEditsUndone: 0,
    channels: [],
    hintsAllowed: true,
    // A one-phase problem has no intro: index 0 is empty and index 1 is the statement.
    brief: ['', 'Find two indices whose values add up to the target.'],
    notes: '',
    events: [],
    result: null,
    ...over,
  };
}

export function renderWith(ui: ReactNode, client: Partial<EngineClient>) {
  const services: AppServices = {
    client: client as EngineClient,
    languages: [javaEditor],
    sections: [],
    go: vi.fn(),
    notify: vi.fn(),
  };
  return { services, ...render(<AppContext.Provider value={services}>{ui}</AppContext.Provider>) };
}
