import { useEffect, useState } from 'react';
import type { ProblemSummary } from '@intervue/local-engine-client';
import { failed, useApp } from '../app-context.ts';
import { Markdown } from '../components/Markdown.tsx';
import { TagChips } from '../components/TagChips.tsx';

/**
 * A problem's answer: its walkthrough, with the steps of a strong round, the prompts it uses and the
 * techniques behind it. Open from a problem card at any time and from the result page.
 */
export function AnswerPage({ problemId, title }: { problemId: string; title: string }) {
  const { client, notify } = useApp();
  const [text, setText] = useState<string | null>(null);
  const [problem, setProblem] = useState<ProblemSummary | null>(null);

  useEffect(() => {
    client.walkthrough(problemId).then((answer) => setText(answer.text), failed(notify));
    client.problems().then((all) => setProblem(all.find((p) => p.id === problemId) ?? null), failed(notify));
  }, [client, notify, problemId]);

  return (
    <div className="page">
      <header className="page-head">
        <div>
          <p className="eyebrow">Answer</p>
          <h1>{title}</h1>
          {problem && <TagChips problem={problem} />}
        </div>
      </header>
      <div className="result-body">{text !== null && <Markdown source={text || 'No walkthrough yet.'} wide />}</div>
    </div>
  );
}
