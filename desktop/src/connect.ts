// Where the engine is: the shell started it and knows its port and token; in a plain browser the
// Vite dev proxy reaches it and adds the token.
import type { EngineConnection } from '@intervue/local-engine-client';

export interface ShellBridge {
  isTauri: () => boolean;
  invoke: (command: string) => Promise<unknown>;
}

export async function connect({ isTauri, invoke }: ShellBridge): Promise<EngineConnection> {
  if (!isTauri()) return { baseUrl: '/engine' };
  // The shape is fixed by the Rust command `engine_connection` (src-tauri/src/lib.rs).
  const { port, token } = (await invoke('engine_connection')) as { port: number; token: string };
  return { baseUrl: `http://127.0.0.1:${port}`, token };
}
