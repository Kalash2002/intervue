import { createServer, type Server } from 'node:http';
import type { AddressInfo } from 'node:net';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';
import { createEngineClient, EngineError } from '../src/index.ts';

let server: Server;
let baseUrl: string;
let lines: unknown[] = [];
let seenBody = '';
let seenUrl = '';

// Writes each queued NDJSON line with a pause, the way the engine streams a model reply.
beforeAll(async () => {
  server = createServer((req, res) => {
    seenUrl = req.url ?? '';
    let body = '';
    req.on('data', (c) => (body += c));
    req.on('end', async () => {
      seenBody = body;
      res.writeHead(200, { 'Content-Type': 'application/x-ndjson' });
      for (const line of lines) {
        res.write(`${JSON.stringify(line)}\n`);
        await new Promise((r) => setTimeout(r, 20));
      }
      res.end();
    });
  });
  await new Promise<void>((resolve) => server.listen(0, '127.0.0.1', resolve));
  baseUrl = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
});

afterAll(() => new Promise<void>((resolve) => server.close(() => resolve())));

describe('streaming assistant calls', () => {
  it('passes deltas in order and resolves with the done result', async () => {
    lines = [
      { t: 'delta', text: 'Look ' },
      { t: 'delta', text: 'at line 15.' },
      { t: 'done', result: 'Look at line 15.' },
    ];
    const deltas: string[] = [];

    const reply = await createEngineClient({ baseUrl }).ask('s1', 'fix it', 'helpful', (d) => deltas.push(d));

    expect(deltas).toEqual(['Look ', 'at line 15.']);
    expect(reply).toBe('Look at line 15.');
    expect(seenUrl).toBe('/sessions/s1/ask');
    expect(JSON.parse(seenBody)).toEqual({ message: 'fix it', rules: 'helpful' });
  });

  it('rejects with the reason from an error line', async () => {
    lines = [{ t: 'error', reason: 'Assistant unavailable: sign-in arrives in Phase 1b' }];

    const call = createEngineClient({ baseUrl }).ask('s1', 'hi', 'helpful', () => {});

    await expect(call).rejects.toBeInstanceOf(EngineError);
    await expect(call).rejects.toThrow('Assistant unavailable: sign-in arrives in Phase 1b');
  });

  it('resolves Write code with the edit reply', async () => {
    lines = [{ t: 'done', result: { replyId: 'w1', note: 'No edit.', outcomes: [] } }];

    const reply = await createEngineClient({ baseUrl }).write('s1', 'fix it', 'interview');

    expect(reply.note).toBe('No edit.');
    expect(seenUrl).toBe('/sessions/s1/write');
  });

  it('streams a hint explanation for a phase', async () => {
    lines = [
      { t: 'delta', text: 'Where.' },
      { t: 'done', result: 'Where.' },
    ];
    const deltas: string[] = [];

    expect(await createEngineClient({ baseUrl }).explainHint('s1', 2, (d) => deltas.push(d))).toBe('Where.');
    expect(JSON.parse(seenBody)).toEqual({ phase: 2 });
    expect(deltas).toEqual(['Where.']);
  });

  it('treats a stream that ends without done as an error', async () => {
    lines = [{ t: 'delta', text: 'half' }];

    await expect(createEngineClient({ baseUrl }).ask('s1', 'hi', 'helpful', () => {})).rejects.toThrow(/ended early/);
  });
});
