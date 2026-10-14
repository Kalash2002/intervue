// Reads the engine's NDJSON stream (engine/api LocalApi.stream): delta lines while the model
// answers, then exactly one done or error line.

interface StreamLine {
  t: 'delta' | 'done' | 'error';
  text?: string;
  result?: unknown;
  reason?: string;
}

/** The body as whole lines, however the bytes were chunked on the way. */
async function* lines(body: ReadableStream<Uint8Array>): AsyncGenerator<string> {
  const reader = body.getReader();
  const decoder = new TextDecoder();
  let pending = '';
  for (;;) {
    const { value, done } = await reader.read();
    // stream mode keeps a multi-byte character split across chunks intact.
    pending += decoder.decode(value, { stream: !done });
    const parts = pending.split('\n');
    pending = done ? '' : (parts.pop() ?? '');
    yield* parts.filter((p) => p.trim() !== '');
    if (done) return;
  }
}

/** Calls `onDelta` for each delta, resolves with the done result, rejects with the error reason. */
export async function readStream<T>(
  response: Response,
  onDelta: (text: string) => void,
  fail: (reason: string) => Error,
): Promise<T> {
  if (!response.body) throw fail('The engine sent no stream.');
  for await (const raw of lines(response.body)) {
    const line = JSON.parse(raw) as StreamLine;
    if (line.t === 'error') throw fail(line.reason ?? 'The assistant call failed.');
    if (line.t === 'done') return line.result as T;
    onDelta(line.text ?? '');
  }
  throw fail('The assistant reply ended early.');
}
