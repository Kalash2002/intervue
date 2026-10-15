import { useEffect, useState } from 'react';
import { failed, useApp } from '../app-context.ts';
import { Markdown } from '../components/Markdown.tsx';
import { TopBar } from '../components/TopBar.tsx';

/**
 * A problem's answer: its walkthrough, with the steps of a strong round, the prompts it uses and the
 * techniques behind it. Open from the dashboard at any time and from the result page.
 */
export function AnswerPage({ problemId, title }: { problemId: string; title: string }) {
  const { client, go, notify } = useApp();
  const [text, setText] = useState<string | null>(null);

  useEffect(() => {
    client.walkthrough(problemId).then((answer) => setText(answer.text), failed(notify));
  }, [client, notify, problemId]);

  return (
    <div className="result">
      <TopBar onHome={() => go({ name: 'dashboard' })}>
        <div className="title">Answer: {title}</div>
        <div className="spacer" />
      </TopBar>
      <div className="result-body">{text !== null && <Markdown source={text || 'No walkthrough yet.'} wide />}</div>
    </div>
  );
}
