//! The desktop shell (ADR 0003): one window with the React UI, and the local engine as a child
//! process started here in Rust. The UI asks for the engine's port and token with one command.

mod engine;

use std::path::PathBuf;
use std::sync::Mutex;

use engine::{Connection, Engine, EnginePaths};
use tauri::{AppHandle, Manager, RunEvent};

/// The started engine, or why it did not start; the UI shows the reason instead of a blank window.
struct EngineState(Mutex<Result<Engine, String>>);

#[tauri::command]
fn engine_connection(state: tauri::State<'_, EngineState>) -> Result<Connection, String> {
    let guard = state
        .0
        .lock()
        .map_err(|_| "The engine state is unavailable.".to_string())?;
    match &*guard {
        Ok(engine) => Ok(engine.connection.clone()),
        Err(message) => Err(message.clone()),
    }
}

fn engine_paths(app: &AppHandle) -> Result<EnginePaths, String> {
    let resources = app.path().resource_dir().map_err(|e| e.to_string())?;
    let data = app.path().app_data_dir().map_err(|e| e.to_string())?;
    // Debug builds run from the repository, where nothing is bundled yet.
    let dev_root =
        cfg!(debug_assertions).then(|| PathBuf::from(env!("CARGO_MANIFEST_DIR")).join("../.."));
    Ok(engine::resolve(&resources, data, dev_root.as_deref()))
}

fn stop_engine(app: &AppHandle) {
    if let Some(state) = app.try_state::<EngineState>() {
        if let Ok(mut guard) = state.0.lock() {
            if let Ok(engine) =
                std::mem::replace(&mut *guard, Err("The engine was stopped.".into()))
            {
                engine.stop();
            }
        }
    }
}

pub fn run() {
    let app = tauri::Builder::default()
        .setup(|app| {
            // Developer mode (the Claude CLI route, PLAN.md 2.5) exists only in debug builds.
            let developer = cfg!(debug_assertions);
            let started =
                engine_paths(app.handle()).and_then(|paths| Engine::start(&paths, developer));
            app.manage(EngineState(Mutex::new(started)));
            Ok(())
        })
        .invoke_handler(tauri::generate_handler![engine_connection])
        .build(tauri::generate_context!())
        .expect("Intervue could not create its window from the bundled Tauri configuration");
    app.run(|handle, event| {
        if let RunEvent::Exit = event {
            stop_engine(handle);
        }
    });
}
