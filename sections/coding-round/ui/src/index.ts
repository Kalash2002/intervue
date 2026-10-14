// The coding round's UI for the app (PLAN.md 5.2, sections/<id>/ui): its room and its result parts.
import type { SectionUi } from '@intervue/ui';
import { ResultParts } from './result/ResultParts.tsx';
import { SessionRoom } from './room/SessionRoom.tsx';

export const codingRoundUi: SectionUi = { Room: SessionRoom, Result: ResultParts };
