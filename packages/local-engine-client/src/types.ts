// Shapes of the engine's JSON replies; they mirror the Java records in engine/ one to one.

export type SessionType = 'INTERVIEW' | 'PRACTICE';

export interface ProblemSummary {
  id: string;
  title: string;
  summary: string;
  difficulty: string;
  minutes: number;
  phases: number;
}

export interface PhaseResult {
  phaseId: number;
  title: string;
  weight: number;
  allOrNothing: boolean;
  passed: number;
  failed: number;
  timeouts: number;
  notRun: number;
}

export interface ScorePart {
  name: string;
  points: number;
  max: number;
  note: string;
}

export interface Score {
  total: number;
  band: string;
  solved: boolean;
  /** "Interview" or "Practice"; using the assistant never changes it (ADR 0006). */
  label: string;
  parts: ScorePart[];
}

export interface TimelineReport {
  activeMs: number;
  runs: number;
  compileErrors: number;
  prompts: number;
  firstRunMs: number;
  firstPromptMs: number;
  longestGapWithoutRunMs: number;
  longestPromptStreak: number;
  phasesSkipped: number;
}

export interface SessionResult {
  score: Score;
  finalResults: PhaseResult[];
  finalCompileError: boolean;
  report: TimelineReport;
  walkthrough: string;
  followUps: string[];
}

export interface EventView {
  seq: number;
  tMs: number;
  type: string;
  data: Record<string, unknown>;
}

export interface SessionView {
  id: string;
  problemId: string;
  title: string;
  type: SessionType;
  minutes: number;
  activeMs: number;
  finished: boolean;
  assisted: boolean;
  unlocked: number;
  phases: number;
  /** Phase id (as a JSON key) to the active ms when it first passed. */
  phasesPassed: Record<string, number>;
  /** Phase id to the highest hint level revealed. */
  hintsUsed: Record<string, number>;
  aiEditsActive: number;
  aiEditsApplied: number;
  aiEditsUndone: number;
  /** The intro, then one entry per phase; null while that phase is locked. */
  brief: (string | null)[];
  notes: string;
  events: EventView[];
  result: SessionResult | null;
}

export interface SessionSummary {
  id: string;
  problemId: string;
  type: SessionType;
  startedAt: number;
  finished: boolean;
  total: number | null;
  label: string | null;
  solved: boolean;
  countsAsSolved: boolean;
}

export interface FileEntry {
  path: string;
  readOnly: boolean;
}

export interface SourceSymbol {
  name: string;
  kind: string;
  container: string;
  detail: string;
  path: string;
  line: number;
  column: number;
}

export interface SourceDiagnostic {
  path: string;
  line: number;
  column: number;
  severity: 'error' | 'warning' | 'note';
  message: string;
}

export interface RunView {
  target: string;
  compileError: boolean;
  timedOut: boolean;
  output: string;
  diagnostics: SourceDiagnostic[];
  phases: PhaseResult[];
  newlyPassed: number[];
  unlocked: number;
  aiEditsActive: number;
}

export interface HintView {
  phase: number;
  level: number;
  text: string;
}

export interface EditOutcome {
  id: string;
  replyId: string;
  path: string;
  op: string;
  status: 'APPLIED' | 'SUGGESTION' | 'NOT_ALLOWED';
  message: string;
  hunks: number;
  beforeHash: string | null;
  afterHash: string | null;
}

export interface EditReply {
  replyId: string;
  note: string;
  outcomes: EditOutcome[];
}

export interface UndoResult {
  id: string;
  undone: boolean;
  /** When the file changed after the edit: the user's version against the one before the edit. */
  diff: string | null;
}

/** Whether the assistant can answer; `reason` says why not (PLAN.md 2.5, Phase 1ab). */
export interface AiStatus {
  developer: boolean;
  available: boolean;
  reason: string | null;
}

/** Practice may pick the rules; Interview sessions always get interview rules. */
export type RulesChoice = 'helpful' | 'interview';

export interface ChatTurn {
  role: 'user' | 'assistant';
  text: string;
  tMs: number;
}

/** The written review: pending until it lands, then ready (text) or unavailable (reason). */
export interface Review {
  status: 'pending' | 'ready' | 'unavailable';
  text: string | null;
  reason: string | null;
}

export interface CheckResult {
  id: string;
  title: string;
  blocking: boolean;
  status: 'PASS' | 'WARN' | 'FAIL';
  message: string;
  measured: string | null;
  ranAt: number;
}

export interface DoctorReport {
  ready: boolean;
  results: CheckResult[];
}
