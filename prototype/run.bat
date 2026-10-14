@echo off
rem Starts Dryrun. Needs a JDK (Java 17 or newer) on PATH. Close this window or press Ctrl+C to stop.
cd /d "%~dp0"
where java >nul 2>nul
if errorlevel 1 (
  echo Java was not found on PATH. Install a JDK 17 or newer, then run this file again.
  pause
  exit /b 1
)
java server\Server.java %*
if errorlevel 1 pause
