import { createServer, type IncomingMessage, type Server } from 'node:http';
import type { AddressInfo } from 'node:net';
import { afterAll, beforeAll, beforeEach, describe, expect, it } from 'vitest';
import { createEngineClient, EngineError } from '../src/index.ts';

interface Seen {
  method: string;
  url: string;
  auth: string | undefined;
  body: string;
}

let server: Server;
let baseUrl: string;
let seen: Seen[];
let reply: { status: number; body: unknown };

const read = (req: IncomingMessage) =>
  new Promise<string>((resolve) => {
    let body = '';
    req.on('data', (chunk) => (body += chunk));
    req.on('end', () => resolve(body));
  });

beforeAll(async () => {
  server = createServer(async (req, res) => {
    seen.push({ method: req.method ?? '', url: req.url ?? '', auth: req.headers.authorization, body: await read(req) });
    res.writeHead(reply.status, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(reply.body));
  });
  await new Promise<void>((resolve) => server.listen(0, '127.0.0.1', resolve));
  baseUrl = `http://127.0.0.1:${(server.address() as AddressInfo).port}`;
});

afterAll(() => new Promise<void>((resolve) => server.close(() => resolve())));

beforeEach(() => {
  seen = [];
  reply = { status: 200, body: { ok: true } };
});

describe('engine client', () => {
  it('sends the bearer token on every call', async () => {
    await createEngineClient({ baseUrl, token: 'tok' }).ping();

    expect(seen[0]).toMatchObject({ method: 'GET', url: '/local/ping', auth: 'Bearer tok' });
  });

  it('omits the header when no token is given (the dev proxy adds it)', async () => {
    await createEngineClient({ baseUrl }).ping();

    expect(seen[0].auth).toBeUndefined();
  });

  it('posts JSON bodies and returns the parsed reply', async () => {
    reply.body = { id: 's1', type: 'PRACTICE' };
    const session = await createEngineClient({ baseUrl }).createSession('tiny', 'PRACTICE');

    expect(seen[0]).toMatchObject({ method: 'POST', url: '/sessions' });
    expect(JSON.parse(seen[0].body)).toEqual({ problemId: 'tiny', type: 'PRACTICE' });
    expect(session.id).toBe('s1');
  });

  it('encodes file paths and edit ids into the URL', async () => {
    const client = createEngineClient({ baseUrl });
    reply.body = { text: 'x' };
    await client.readFile('s 1', 'data/a b.txt');
    reply.body = { id: 'r:1', undone: true, diff: null };
    await client.undoEdit('s 1', 'r1:e1');

    expect(seen[0].url).toBe('/sessions/s%201/file?path=data%2Fa%20b.txt');
    expect(seen[1].url).toBe('/sessions/s%201/edits/r1%3Ae1/undo');
  });

  it('turns an error reply into an EngineError with the engine message', async () => {
    reply = { status: 400, body: { error: 'Hints are for Practice sessions.' } };

    const failure = createEngineClient({ baseUrl }).revealHint('s1', 1);

    await expect(failure).rejects.toBeInstanceOf(EngineError);
    await expect(failure).rejects.toMatchObject({ status: 400, message: 'Hints are for Practice sessions.' });
  });

  it('covers every route group', async () => {
    const c = createEngineClient({ baseUrl });
    reply.body = [];
    await c.problems();
    await c.history();
    await c.files('s');
    await c.symbols('s');
    await c.diagnostics('s');
    await c.revealedHints('s', 2);
    await c.fakeEditIds();
    await c.health();
    reply.body = {};
    await c.session('s');
    await c.writeFile('s', 'A.java', 'class A {}');
    await c.createFile('s', 'B.java');
    await c.saveNotes('s', 'n');
    await c.run('s', 'phase:1');
    await c.skip('s');
    await c.replayFakeEdits('s', 'hold-exposure-phase1');
    await c.undoReply('s', 'r1');
    await c.resetFile('s', 'A.java');
    await c.resetProblem('s');
    await c.finish('s');
    await c.runHealth(true);

    expect(seen.map((s) => `${s.method} ${s.url}`)).toEqual([
      'GET /problems',
      'GET /sessions',
      'GET /sessions/s/files',
      'GET /sessions/s/symbols',
      'GET /sessions/s/diagnostics',
      'GET /sessions/s/hints?phase=2',
      'GET /fake-edits',
      'GET /health',
      'GET /sessions/s',
      'PUT /sessions/s/file?path=A.java',
      'POST /sessions/s/files',
      'PUT /sessions/s/notes',
      'POST /sessions/s/run',
      'POST /sessions/s/skip',
      'POST /sessions/s/fake-edits',
      'POST /sessions/s/replies/r1/undo',
      'POST /sessions/s/reset-file',
      'POST /sessions/s/reset-problem',
      'POST /sessions/s/finish',
      'POST /health/run?fast=true',
    ]);
  });
});
