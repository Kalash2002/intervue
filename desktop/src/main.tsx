// The renderer: find the engine, then mount the app. Nothing else lives here (PLAN.md 5.2).
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { invoke, isTauri } from '@tauri-apps/api/core';
import { sectionUis } from '@intervue/sections';
import { javaEditor } from '@intervue/lang-java-editor';
import { createEngineClient } from '@intervue/local-engine-client';
import { App } from '@intervue/ui';
import { connect } from './connect.ts';

const languages = [javaEditor];

const root = createRoot(document.getElementById('root') as HTMLElement);
connect({ isTauri, invoke })
  .then((connection) =>
    root.render(
      <StrictMode>
        <App client={createEngineClient(connection)} sections={sectionUis} languages={languages} />
      </StrictMode>,
    ),
  )
  .catch((error: unknown) => root.render(<p className="loading">The engine did not start: {String(error)}</p>));
