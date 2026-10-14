import { useEffect, useState } from 'react';
import type { Review } from '@intervue/local-engine-client';
import { failed, Markdown, useApp } from '@intervue/ui';

/**
 * The written review from `prompts/feedback.md`, requested by the engine at finish. It can take a
 * minute, so the page polls until it is ready or the engine says why there is none.
 */
export function WrittenReview({ sessionId, pollMs = 3000 }: { sessionId: string; pollMs?: number }) {
  const { client, notify } = useApp();
  const [review, setReview] = useState<Review | null>(null);

  useEffect(() => {
    let live = true;
    let timer: ReturnType<typeof setTimeout> | undefined;
    const poll = () =>
      client
        .review(sessionId)
        .then((r) => {
          if (!live) return;
          setReview(r);
          if (r.status === 'pending') timer = setTimeout(poll, pollMs);
        })
        .catch(failed(notify));
    poll();
    return () => {
      live = false;
      clearTimeout(timer);
    };
  }, [client, notify, pollMs, sessionId]);

  if (!review || review.status === 'pending') {
    return <p className="hint">Writing the review… it usually takes under a minute.</p>;
  }
  if (review.status === 'unavailable') {
    return <p className="assistant-off">Review unavailable: {review.reason}</p>;
  }
  return <Markdown source={review.text ?? ''} wide />;
}
