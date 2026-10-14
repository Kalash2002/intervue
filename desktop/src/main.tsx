// The renderer: find the engine, then mount the app. Nothing else lives here (PLAN.md 5.2).
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { invoke, isTauri } from '@tauri-apps/api/core';
import { codingRoundUi } from '@intervue/coding-round-ui';
import { javaEditor } from '@intervue/lang-java-editor';
import { createEngineClient, type EngineConnection } from '@intervue/local-engine-client';
import { App } from '@intervue/ui';

const languages = [javaEditor];

/** In the shell the Rust side started the engine; in a plain browser the Vite proxy reaches it. */
async function connect(): Promise<EngineConnection> {
  if (!isTauri()) return { baseUrl: '/engine' };
  const { port, token } = await invoke<{ port: number; token: string }>('engine_connection');
  return { baseUrl: `http://127.0.0.1:${port}`, token };
}

const root = createRoot(document.getElementById('root') as HTMLElement);
connect()
  .then((connection) =>
    root.render(
      <StrictMode>
        <App client={createEngineClient(connection)} section={codingRoundUi} languages={languages} />
      </StrictMode>,
    ),
  )
  .catch((error: unknown) => root.render(<p className="loading">The engine did not start: {String(error)}</p>));
