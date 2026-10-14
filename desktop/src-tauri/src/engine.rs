//! Starts the local engine (`java -jar engine.jar`) as a child process and holds its port and
//! per-launch token for the UI. No domain logic: find the files, start, wait for the ready line,
//! stop on exit. The ready line and the token are never written to any log.

use std::ffi::OsString;
use std::fs::File;
use std::io::{BufRead, BufReader};
use std::path::{Path, PathBuf};
use std::process::{Child, ChildStdout, Command, Stdio};
use std::sync::mpsc;
use std::time::Duration;

pub const READY_PREFIX: &str = "INTERVUE_ENGINE_READY port=";
const START_TIMEOUT: Duration = Duration::from_secs(60);

/// Where the runtime, the engine, the content and the user's data live.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct EnginePaths {
    pub java: PathBuf,
    pub jar: PathBuf,
    pub content: PathBuf,
    pub data: PathBuf,
}

/// What the UI needs to call the engine.
#[derive(Debug, Clone, serde::Serialize)]
pub struct Connection {
    pub port: u16,
    pub token: String,
}

pub struct Engine {
    child: Child,
    pub connection: Connection,
}

/// Bundled files first; in debug builds, the repository's build output when nothing is bundled.
pub fn resolve(resources: &Path, data: PathBuf, dev_root: Option<&Path>) -> EnginePaths {
    let java_name = if cfg!(windows) { "java.exe" } else { "java" };
    let bundled_java = resources.join("runtime").join("bin").join(java_name);
    let pick = |bundled: PathBuf, dev: &str| match dev_root {
        Some(root) if !bundled.exists() => root.join(dev),
        _ => bundled,
    };
    EnginePaths {
        java: if bundled_java.is_file() {
            bundled_java
        } else {
            PathBuf::from("java")
        },
        jar: pick(
            resources.join("engine").join("engine.jar"),
            "engine/build/engine/engine.jar",
        ),
        content: pick(resources.join("content"), "content"),
        data,
    }
}

/// Refuses to start with a missing engine or content folder, naming what is missing.
pub fn validate(paths: &EnginePaths) -> Result<(), String> {
    if !paths.jar.is_file() {
        return Err(format!("The engine is missing: {}", paths.jar.display()));
    }
    if !paths.content.join("problems").is_dir() {
        return Err(format!(
            "The problems folder is missing: {}",
            paths.content.join("problems").display()
        ));
    }
    Ok(())
}

pub fn engine_args(paths: &EnginePaths) -> Vec<OsString> {
    vec![
        "-jar".into(),
        paths.jar.clone().into(),
        "--data".into(),
        paths.data.clone().into(),
        "--content".into(),
        paths.content.clone().into(),
    ]
}

pub fn parse_ready(line: &str) -> Option<u16> {
    line.trim().strip_prefix(READY_PREFIX)?.parse().ok()
}

/// 32 random bytes as hex; the engine requires it on every request.
pub fn new_token() -> Result<String, String> {
    let mut bytes = [0u8; 32];
    getrandom::fill(&mut bytes)
        .map_err(|e| format!("No random source for the engine token: {e}"))?;
    Ok(bytes.iter().map(|b| format!("{b:02x}")).collect())
}

impl Engine {
    pub fn start(paths: &EnginePaths) -> Result<Engine, String> {
        validate(paths)?;
        std::fs::create_dir_all(&paths.data)
            .map_err(|e| format!("Cannot create {}: {e}", paths.data.display()))?;
        let log = File::create(paths.data.join("engine.log"))
            .map_err(|e| format!("Cannot write the engine log: {e}"))?;
        let token = new_token()?;
        let mut command = Command::new(&paths.java);
        command
            .args(engine_args(paths))
            .env("INTERVUE_ENGINE_TOKEN", &token)
            .stdin(Stdio::null())
            .stdout(Stdio::piped())
            .stderr(log);
        hide_console(&mut command);
        let mut child = command
            .spawn()
            .map_err(|e| format!("Cannot start {}: {e}", paths.java.display()))?;
        let stdout = child.stdout.take().ok_or("The engine has no output pipe")?;
        match wait_ready(stdout) {
            Ok(port) => Ok(Engine {
                child,
                connection: Connection { port, token },
            }),
            Err(message) => {
                // Best effort: the child may already have exited, which is why it is not ready.
                let _ = child.kill();
                Err(message)
            }
        }
    }

    /// Ends the engine. Its sessions' clocks close the gap at the next start (heartbeat rule).
    pub fn stop(mut self) {
        let _ = self.child.kill();
        let _ = self.child.wait();
    }
}

fn wait_ready(stdout: ChildStdout) -> Result<u16, String> {
    let (tx, rx) = mpsc::channel();
    // The reader keeps draining stdout after the ready line so the engine never blocks on a pipe.
    std::thread::spawn(move || {
        for line in BufReader::new(stdout).lines().map_while(Result::ok) {
            if let Some(port) = parse_ready(&line) {
                let _ = tx.send(port);
            }
        }
    });
    rx.recv_timeout(START_TIMEOUT).map_err(|e| match e {
        mpsc::RecvTimeoutError::Timeout => {
            "The engine did not start within 60 seconds.".to_string()
        }
        mpsc::RecvTimeoutError::Disconnected => {
            "The engine stopped while starting; see engine.log in the data folder.".to_string()
        }
    })
}

#[cfg(windows)]
fn hide_console(command: &mut Command) {
    use std::os::windows::process::CommandExt;
    const CREATE_NO_WINDOW: u32 = 0x0800_0000;
    command.creation_flags(CREATE_NO_WINDOW);
}

#[cfg(not(windows))]
fn hide_console(_command: &mut Command) {}

#[cfg(test)]
mod tests {
    use super::*;

    fn paths(root: &Path) -> EnginePaths {
        EnginePaths {
            java: PathBuf::from("java"),
            jar: root.join("engine.jar"),
            content: root.join("content"),
            data: root.join("data"),
        }
    }

    fn temp_dir(name: &str) -> PathBuf {
        let dir =
            std::env::temp_dir().join(format!("intervue-shell-{name}-{}", std::process::id()));
        let _ = std::fs::remove_dir_all(&dir);
        std::fs::create_dir_all(&dir).expect("temp dir");
        dir
    }

    #[test]
    fn reads_the_port_from_the_ready_line_only() {
        assert_eq!(
            parse_ready("INTERVUE_ENGINE_READY port=52960\r\n"),
            Some(52960)
        );
        assert_eq!(parse_ready("INTERVUE_ENGINE_READY port=notaport"), None);
        assert_eq!(parse_ready("Picked up JAVA_TOOL_OPTIONS"), None);
    }

    #[test]
    fn passes_jar_data_and_content_in_that_order() {
        let p = paths(Path::new("r"));
        let args: Vec<String> = engine_args(&p)
            .iter()
            .map(|a| a.to_string_lossy().into_owned())
            .collect();
        assert_eq!(args[0], "-jar");
        assert_eq!(args[2], "--data");
        assert_eq!(args[4], "--content");
        assert_eq!(PathBuf::from(&args[5]), p.content);
    }

    #[test]
    fn tokens_are_64_hex_characters_and_differ() {
        let a = new_token().expect("token");
        let b = new_token().expect("token");
        assert_eq!(a.len(), 64);
        assert!(a.chars().all(|c| c.is_ascii_hexdigit()));
        assert_ne!(a, b);
    }

    #[test]
    fn refuses_to_start_without_the_engine_or_the_problems() {
        let root = temp_dir("validate");
        let p = paths(&root);
        assert!(validate(&p).unwrap_err().contains("engine is missing"));
        std::fs::write(&p.jar, b"jar").expect("write jar");
        assert!(validate(&p).unwrap_err().contains("problems folder"));
        std::fs::create_dir_all(p.content.join("problems")).expect("problems");
        assert!(validate(&p).is_ok());
    }

    #[test]
    fn prefers_bundled_files_and_falls_back_to_the_repository_in_dev() {
        let resources = temp_dir("resolve");
        let dev = Path::new("repo");
        let unbundled = resolve(&resources, PathBuf::from("d"), Some(dev));
        assert_eq!(unbundled.jar, dev.join("engine/build/engine/engine.jar"));
        assert_eq!(unbundled.java, PathBuf::from("java"));

        std::fs::create_dir_all(resources.join("engine")).expect("engine dir");
        std::fs::write(resources.join("engine").join("engine.jar"), b"jar").expect("jar");
        let bundled = resolve(&resources, PathBuf::from("d"), Some(dev));
        assert_eq!(bundled.jar, resources.join("engine").join("engine.jar"));
        assert_eq!(
            resolve(&resources, PathBuf::from("d"), None).content,
            resources.join("content")
        );
    }
}
