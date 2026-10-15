import { useEffect, useState } from 'react';
import type { ProblemSummary } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { Markdown } from '../components/Markdown.tsx';
import { TagChips } from '../components/TagChips.tsx';

/**
 * A problem's answer: its walkthrough, with the steps of a strong round, the prompts it uses and the
 * techniques behind it. Open from a problem card at any time and from the result page.
 */
export function AnswerPage({ problem }: { problem: ProblemSummary }) {
  const { client, notify } = useApp();
  const [text, setText] = useState<string | null>(null);

  useEffect(() => {
    client.walkthrough(problem.id).then((answer) => setText(answer.text), failed(notify));
  }, [client, notify, problem.id]);

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <p className="eyebrow">Answer</p>
          <h1>{problem.title}</h1>
          <TagChips problem={problem} />
        </div>
      </header>
      <div className="result-body">{text !== null && <Markdown source={text || 'No walkthrough yet.'} wide />}</div>
    </div>
  );
}
