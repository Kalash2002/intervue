// A problem's tags as chips; each opens its section filtered to that tag. Renders nothing for a
// problem without tags, so callers never check.
import type { ProblemSummary } from '@intervue/local-engine-client';
import { useApp } from '../app-context.ts';
import { Chip } from './Chip.tsx';

export function TagChips({ problem }: { problem: Pick<ProblemSummary, 'section' | 'tags'> }) {
  const { go } = useApp();
  if (problem.tags.length === 0) return null;
  return (
    <p className="chips" aria-label="Tags">
      {problem.tags.map((tag) => (
        <Chip key={tag} onClick={() => go({ name: 'section', sectionId: problem.section, tag })}>
          {tag}
        </Chip>
      ))}
    </p>
  );
}
