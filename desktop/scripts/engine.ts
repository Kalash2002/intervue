// Builds engine.jar and its lib/ folder (Gradle task :engine:engineDist) for the shell to start.
import { execSync } from 'node:child_process';
import { join } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = fileURLToPath(new URL('../../', import.meta.url));
const gradle = join(root, process.platform === 'win32' ? 'gradlew.bat' : 'gradlew');
execSync(`"${gradle}" :engine:engineDist -q`, { cwd: root, stdio: 'inherit' });
