// The DSA section's UI for the app (sections/<id>/ui): its solving room and its result parts.
import type { SectionUi } from '@intervue/ui';
import section from '../../section.json';
import { DsaResult } from './DsaResult.tsx';
import { SolveRoom } from './SolveRoom.tsx';

export const dsaQuickUi: SectionUi = {
  id: section.id,
  title: section.name,
  description: section.description,
  Room: SolveRoom,
  Result: DsaResult,
};
