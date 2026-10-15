// Typed client for the local engine's HTTP API (engine/src/main/java/si/intervue/engine/api/Routes.java).
import type {
  CheckResult,
  DoctorReport,
  EditReply,
  FileEntry,
  HintView,
  ProblemSummary,
  RunView,
  SessionSummary,
  SessionType,
  SessionView,
  SourceDiagnostic,
  SourceSymbol,
  UndoResult,
} from './types.ts';

export type * from './types.ts';

/** A reply the engine refused or failed; `message` is the engine's own text, fit to show. */
export class EngineError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = 'EngineError';
    this.status = status;
  }
}

export interface EngineConnection {
  /** `http://127.0.0.1:<port>` from the shell, or a same-origin prefix behind the dev proxy. */
  baseUrl: string;
  /** The per-launch token; absent behind the dev proxy, which adds it. */
  token?: string;
}

export type EngineClient = ReturnType<typeof createEngineClient>;

const enc = encodeURIComponent;

export function createEngineClient({ baseUrl, token }: EngineConnection) {
  async function call<T>(method: string, path: string, body?: unknown): Promise<T> {
    const headers: Record<string, string> = {};
    if (token) headers.Authorization = `Bearer ${token}`;
    if (body !== undefined) headers['Content-Type'] = 'application/json';
    const response = await fetch(baseUrl + path, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });
    const parsed = await response.json().catch(() => ({ error: response.statusText }));
    if (!response.ok) throw new EngineError(response.status, parsed.error ?? response.statusText);
    return parsed as T;
  }
  const s = (id: string) => `/sessions/${enc(id)}`;

  return {
    ping: () => call<{ ok: boolean }>('GET', '/local/ping'),
    problems: () => call<ProblemSummary[]>('GET', '/problems'),
    history: () => call<SessionSummary[]>('GET', '/sessions'),
    walkthrough: (problemId: string) => call<{ text: string }>('GET', `/problems/${enc(problemId)}/walkthrough`),
    createSession: (problemId: string, type: SessionType) =>
      call<SessionView>('POST', '/sessions', { problemId, type }),
    session: (id: string) => call<SessionView>('GET', s(id)),
    files: (id: string) => call<FileEntry[]>('GET', `${s(id)}/files`),
    readFile: (id: string, path: string) => call<{ text: string }>('GET', `${s(id)}/file?path=${enc(path)}`),
    writeFile: (id: string, path: string, text: string) =>
      call<{ ok: boolean }>('PUT', `${s(id)}/file?path=${enc(path)}`, { text }),
    createFile: (id: string, path: string) => call<{ ok: boolean }>('POST', `${s(id)}/files`, { path }),
    symbols: (id: string) => call<SourceSymbol[]>('GET', `${s(id)}/symbols`),
    diagnostics: (id: string) => call<SourceDiagnostic[]>('GET', `${s(id)}/diagnostics`),
    saveNotes: (id: string, notes: string) => call<{ ok: boolean }>('PUT', `${s(id)}/notes`, { notes }),
    run: (id: string, target: string) => call<RunView>('POST', `${s(id)}/run`, { target }),
    skip: (id: string) => call<SessionView>('POST', `${s(id)}/skip`),
    revealHint: (id: string, phase: number) => call<HintView>('POST', `${s(id)}/hints`, { phase }),
    revealedHints: (id: string, phase: number) => call<string[]>('GET', `${s(id)}/hints?phase=${phase}`),
    fakeEditIds: () => call<string[]>('GET', '/fake-edits'),
    replayFakeEdits: (id: string, replyId: string) => call<EditReply>('POST', `${s(id)}/fake-edits`, { id: replyId }),
    undoEdit: (id: string, editId: string) => call<UndoResult>('POST', `${s(id)}/edits/${enc(editId)}/undo`),
    undoReply: (id: string, replyId: string) => call<UndoResult[]>('POST', `${s(id)}/replies/${enc(replyId)}/undo`),
    resetFile: (id: string, path: string) => call<{ text: string }>('POST', `${s(id)}/reset-file`, { path }),
    resetProblem: (id: string) => call<{ ok: boolean }>('POST', `${s(id)}/reset-problem`),
    finish: (id: string) => call<SessionView>('POST', `${s(id)}/finish`),
    health: () => call<CheckResult[]>('GET', '/health'),
    runHealth: (fast: boolean) => call<DoctorReport>('POST', `/health/run${fast ? '?fast=true' : ''}`),
  };
}
