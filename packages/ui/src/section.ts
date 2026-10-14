// What a section package's UI (sections/<id>/ui) gives the app: its room and its result parts.
import type { ComponentType } from 'react';
import type { SessionView } from '@intervue/local-engine-client';
import type { TimedSession } from './lib/timed.ts';

export interface SectionRoomProps {
  session: TimedSession;
}

export interface SectionResultProps {
  session: SessionView;
}

export interface SectionUi {
  Room: ComponentType<SectionRoomProps>;
  Result: ComponentType<SectionResultProps>;
}
