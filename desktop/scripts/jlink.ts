// Produces desktop/resources/runtime: the Java runtime the app bundles (PLAN.md 3.2). It runs the
// engine and compiles the candidate's code, so it keeps jdk.compiler. Uses the JDK on JAVA_HOME,
// else the one `java` on PATH belongs to (Temurin 21 on CI; any JDK 21 for a local build).
import { execFileSync, spawnSync } from 'node:child_process';
import { existsSync, rmSync } from 'node:fs';
import { join } from 'node:path';
import { fileURLToPath } from 'node:url';

const MODULES = [
  'java.base',
  'jdk.compiler',
  'jdk.httpserver',
  'java.management',
  'jdk.management',
  'java.sql',
  'jdk.crypto.ec',
];

function javaHome(): string {
  if (process.env.JAVA_HOME) return process.env.JAVA_HOME;
  // -XshowSettings prints to stderr.
  const { stderr } = spawnSync('java', ['-XshowSettings:properties', '-version'], { encoding: 'utf8' });
  const match = /java\.home = (.+)/.exec(stderr ?? '');
  if (!match) throw new Error('Set JAVA_HOME to a JDK 21 before building the runtime.');
  return match[1].trim();
}

const home = javaHome();
const jlink = join(home, 'bin', process.platform === 'win32' ? 'jlink.exe' : 'jlink');
const output = fileURLToPath(new URL('../resources/runtime', import.meta.url));
if (existsSync(output)) rmSync(output, { recursive: true });

execFileSync(
  jlink,
  [
    '--add-modules',
    MODULES.join(','),
    '--strip-debug',
    '--no-header-files',
    '--no-man-pages',
    '--compress',
    'zip-6',
    '--output',
    output,
  ],
  { stdio: 'inherit' },
);
console.log(`Runtime written to ${output}`);
