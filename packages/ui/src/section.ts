// What a section package's UI (sections/<id>/ui) gives the app: its identity for the menu and the
// section page, its room and its result parts (docs/contracts/section-contract.md).
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
  /** The section id, as in section.json and problem.json. */
  id: string;
  /** The menu entry and page title. */
  title: string;
  /** One paragraph under the title of the section page. */
  description: string;
  Room: ComponentType<SectionRoomProps>;
  Result: ComponentType<SectionResultProps>;
}
