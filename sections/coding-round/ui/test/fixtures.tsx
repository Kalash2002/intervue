import { render } from '@testing-library/react';
import type { ReactNode } from 'react';
import type { EngineClient, EventView, SessionView } from '@intervue/local-engine-client';
import { AppContext, type AppServices } from '@intervue/ui';
import { javaEditor } from '@intervue/lang-java-editor';
import { vi } from 'vitest';

export function view(over: Partial<SessionView> = {}): SessionView {
  return {
    id: 's1',
    problemId: 'tiny',
    section: 'coding-round',
    title: 'Tiny',
    type: 'INTERVIEW',
    minutes: 10,
    timed: true,
    activeMs: 0,
    finished: false,
    assisted: false,
    unlocked: 1,
    phases: 2,
    phaseTitles: ['Fix', 'Grow'],
    tags: [],
    phasesPassed: {},
    hintsUsed: {},
    aiEditsActive: 0,
    aiEditsApplied: 0,
    aiEditsUndone: 0,
    channels: ['ASK', 'WRITE_CODE'],
    hintsAllowed: false,
    brief: ['# Intro', '## Phase 1: fix the bug\n\nTwice adds one too many. Fix it and keep the change small.', null],
    notes: '',
    events: [],
    result: null,
    ...over,
  };
}

export const event = (seq: number, type: string, tMs: number, data: Record<string, unknown> = {}): EventView => ({
  seq,
  type,
  tMs,
  data,
});

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
