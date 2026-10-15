// The coding round's UI for the app (PLAN.md 5.2, sections/<id>/ui): its room and its result parts.
import type { SectionUi } from '@intervue/ui';
import section from '../../section.json';
import { ResultParts } from './result/ResultParts.tsx';
import { SessionRoom } from './room/SessionRoom.tsx';

export const codingRoundUi: SectionUi = {
  id: section.id,
  title: section.name,
  description: section.description,
  Room: SessionRoom,
  Result: ResultParts,
};
