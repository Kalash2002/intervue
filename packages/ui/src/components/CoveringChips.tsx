// The chips that open the Answer page of each problem named in a question's coveredBy.
import type { ReactNode } from 'react';
import type { ProblemSummary } from '@intervue/local-engine-client';
import { useApp } from '../app-context.ts';
import { Chip } from './Chip.tsx';

/** `children` come before the chips (a "Covered by" caption); `label` is the group's aria-label. */
export function CoveringChips({
  problems,
  label,
  children,
}: {
  problems: readonly ProblemSummary[];
  label?: string;
  children?: ReactNode;
}) {
  const { go } = useApp();
  return (
    <p className="chips" aria-label={label}>
      {children}
      {problems.map((q) => (
        <Chip key={q.id} onClick={() => go({ name: 'answer', problem: q })}>
          {q.title}
        </Chip>
      ))}
    </p>
  );
}
