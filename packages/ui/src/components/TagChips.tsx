// A problem's tags as chips; each opens its section filtered to that tag. Renders nothing for a
// problem without tags, so callers never check.
import type { ProblemSummary } from '@intervue/local-engine-client';
import { useApp } from '../app-context.ts';

export function TagChips({ problem }: { problem: Pick<ProblemSummary, 'section' | 'tags'> }) {
  const { go } = useApp();
  if (problem.tags.length === 0) return null;
  return (
    <p className="tags" aria-label="Tags">
      {problem.tags.map((tag) => (
        <button
          key={tag}
          type="button"
          className="chip"
          onClick={() => go({ name: 'section', sectionId: problem.section, tag })}
        >
          {tag}
        </button>
      ))}
    </p>
  );
}
