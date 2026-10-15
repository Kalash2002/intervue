// The list of sections the app offers, in menu order.
import { codingRoundUi } from '@intervue/coding-round-ui';
import { dsaQuickUi } from '@intervue/dsa-quick-ui';
import type { SectionUi } from '@intervue/ui';

/**
 * Every section the app shows. A new section is registered here, and only here: the desktop
 * shell must not list sections itself (docs/adr/0008).
 */
export const sectionUis: readonly SectionUi[] = [codingRoundUi, dsaQuickUi];
