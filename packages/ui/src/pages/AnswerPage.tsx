import type { SessionView } from '@intervue/local-engine-client';
import { useApp } from '../app-context.ts';
import { Markdown } from '../components/Markdown.tsx';
import { TopBar } from '../components/TopBar.tsx';

/**
 * The problem's answer: its walkthrough, with the steps of a strong round, the prompts it uses and
 * the techniques behind it. Opened from a finished session, so it never spoils an unattempted problem.
 */
export function AnswerPage({ session }: { session: SessionView }) {
  const { go } = useApp();
  return (
    <div className="result">
      <TopBar onHome={() => go({ name: 'dashboard' })}>
        <div className="title">Answer: {session.title}</div>
        <div className="spacer" />
        <button className="ghost" type="button" onClick={() => go({ name: 'result', session })}>
          Your result
        </button>
      </TopBar>
      <div className="result-body">
        <Markdown source={session.result?.walkthrough || 'No walkthrough yet.'} wide />
      </div>
    </div>
  );
}
