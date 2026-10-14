-- Practice may run against the clock (Phase 1ab, PLAN.md 2.8); Interview sessions are always timed.
ALTER TABLE sessions ADD COLUMN timed INTEGER NOT NULL DEFAULT 0;
