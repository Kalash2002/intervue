-- Schema v1 (PLAN.md 2.2, ADR 0004): the tables Phase 1a uses. Times are epoch milliseconds;
-- JSON columns hold text written by the engine. Never edit this file once shipped; add V2.

CREATE TABLE sessions (
  id TEXT PRIMARY KEY,
  problem_id TEXT NOT NULL,
  problem_version INTEGER NOT NULL,
  variant_lang TEXT NOT NULL,
  section_id TEXT NOT NULL,
  pack_id TEXT,
  session_type TEXT NOT NULL CHECK (session_type IN ('interview', 'practice')),
  started_at INTEGER NOT NULL,
  finished_at INTEGER,
  minutes INTEGER NOT NULL,
  active_ms INTEGER NOT NULL DEFAULT 0,
  unlocked INTEGER NOT NULL DEFAULT 1,
  assisted INTEGER NOT NULL DEFAULT 0,
  hints_used TEXT NOT NULL DEFAULT '{}',
  final_results TEXT,
  score TEXT,
  notes TEXT NOT NULL DEFAULT '',
  notes_updated_at INTEGER,
  feedback TEXT
);

CREATE TABLE events (
  session_id TEXT NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
  seq INTEGER NOT NULL,
  t_ms INTEGER NOT NULL,
  type TEXT NOT NULL,
  data TEXT NOT NULL DEFAULT '{}',
  PRIMARY KEY (session_id, seq)
);

CREATE TABLE edits (
  session_id TEXT NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
  edit_id TEXT NOT NULL,
  reply_id TEXT NOT NULL,
  path TEXT NOT NULL,
  op TEXT NOT NULL CHECK (op IN ('replace', 'create')),
  before_hash TEXT,
  after_hash TEXT NOT NULL,
  snapshot_path TEXT,
  undone_at INTEGER,
  PRIMARY KEY (session_id, edit_id)
);

CREATE TABLE chat_turns (
  session_id TEXT NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
  seq INTEGER NOT NULL,
  role TEXT NOT NULL,
  text TEXT NOT NULL,
  model TEXT,
  t_ms INTEGER NOT NULL,
  PRIMARY KEY (session_id, seq)
);

CREATE TABLE session_files (
  session_id TEXT NOT NULL REFERENCES sessions(id) ON DELETE CASCADE,
  path TEXT NOT NULL,
  content TEXT NOT NULL,
  is_diff_from_starter INTEGER NOT NULL,
  PRIMARY KEY (session_id, path)
);

CREATE TABLE settings (
  key TEXT PRIMARY KEY,
  value TEXT NOT NULL
);

CREATE TABLE health_results (
  check_id TEXT PRIMARY KEY,
  status TEXT NOT NULL,
  message TEXT,
  measured TEXT,
  ran_at INTEGER NOT NULL
);
