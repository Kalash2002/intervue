"use strict";

// ---------------------------------------------------------------- helpers

const $ = (id) => document.getElementById(id);

async function api(method, path, body) {
  const res = await fetch("/api/" + path, {
    method,
    headers: { "X-Dryrun": "1", "Content-Type": "application/json" },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  let data = {};
  try { data = await res.json(); } catch (e) { /* empty body */ }
  if (!res.ok) throw new Error(data.error || "The request failed (" + res.status + ").");
  return data;
}

/** POSTs and reads a stream of JSON lines: {t:"delta",text}, then {t:"done"} or {t:"error",message}. */
async function stream(path, body, onDelta) {
  const res = await fetch("/api/" + path, {
    method: "POST",
    headers: { "X-Dryrun": "1", "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    let data = {};
    try { data = await res.json(); } catch (e) { /* ignore */ }
    throw new Error(data.error || "The request failed (" + res.status + ").");
  }
  const reader = res.body.getReader();
  const decoder = new TextDecoder();
  let buffered = "";
  let last = null;
  for (;;) {
    const { value, done } = await reader.read();
    if (done) break;
    buffered += decoder.decode(value, { stream: true });
    let nl;
    while ((nl = buffered.indexOf("\n")) >= 0) {
      const line = buffered.slice(0, nl);
      buffered = buffered.slice(nl + 1);
      if (!line.trim()) continue;
      const msg = JSON.parse(line);
      if (msg.t === "delta") onDelta(msg.text);
      else last = msg;
    }
  }
  if (!last) throw new Error("The connection closed before the reply finished.");
  if (last.t === "error") {
    const err = new Error(last.message);
    err.events = last.events;
    throw err;
  }
  return last;
}

const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));

function clockText(ms) {
  const s = Math.max(0, Math.floor(ms / 1000));
  return String(Math.floor(s / 60)).padStart(2, "0") + ":" + String(s % 60).padStart(2, "0");
}

let toastTimer = null;
function toast(text, kind) {
  const el = $("toast");
  el.textContent = text;
  el.className = "toast" + (kind ? " " + kind : "");
  el.hidden = false;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => { el.hidden = true; }, 5000);
}

function ask({ title, text, ok, input }) {
  return new Promise((resolve) => {
    $("modalTitle").textContent = title;
    $("modalText").textContent = text || "";
    $("modalOk").textContent = ok || "OK";
    const field = $("modalInput");
    field.hidden = input === undefined;
    field.value = input || "";
    $("modal").hidden = false;
    (input === undefined ? $("modalOk") : field).focus();
    const close = (value) => {
      $("modal").hidden = true;
      $("modalOk").onclick = $("modalCancel").onclick = field.onkeydown = null;
      resolve(value);
    };
    $("modalOk").onclick = () => close(input === undefined ? true : field.value.trim());
    $("modalCancel").onclick = () => close(null);
    field.onkeydown = (e) => { if (e.key === "Enter") close(field.value.trim()); };
  });
}

// ---------------------------------------------------------------- Markdown (small, escapes everything first)

function inlineMd(text) {
  let s = esc(text);
  const codes = [];
  s = s.replace(/`([^`\n]+)`/g, (m, c) => { codes.push(c); return "\u0000" + (codes.length - 1) + "\u0000"; });
  s = s.replace(/\*\*([^*\n]+)\*\*/g, "<strong>$1</strong>");
  s = s.replace(/(^|[\s(])\*([^*\n]+)\*(?=[\s).,;:!?]|$)/g, "$1<em>$2</em>");
  s = s.replace(/\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>');
  return s.replace(/\u0000(\d+)\u0000/g, (m, i) => "<code>" + codes[+i] + "</code>");
}

function renderMd(src) {
  const lines = String(src).replace(/\r/g, "").split("\n");
  const out = [];
  let i = 0;
  let para = [];
  const flush = () => { if (para.length) { out.push("<p>" + inlineMd(para.join(" ")) + "</p>"); para = []; } };
  while (i < lines.length) {
    const line = lines[i];
    const fence = line.match(/^\s*```(\w*)\s*$/);
    if (fence) {
      flush();
      const code = [];
      i++;
      while (i < lines.length && !/^\s*```\s*$/.test(lines[i])) code.push(lines[i++]);
      i++;
      out.push('<pre><button class="ghost copy" type="button">Copy</button><code>' + esc(code.join("\n")) + "</code></pre>");
      continue;
    }
    const heading = line.match(/^(#{1,4})\s+(.*)$/);
    if (heading) {
      flush();
      const level = Math.min(3, heading[1].length);
      out.push("<h" + level + ">" + inlineMd(heading[2]) + "</h" + level + ">");
      i++;
      continue;
    }
    if (/^\s*\|.*\|\s*$/.test(line) && i + 1 < lines.length && /^\s*\|[\s:|-]+\|\s*$/.test(lines[i + 1])) {
      flush();
      const cells = (row) => row.trim().replace(/^\||\|$/g, "").split("|").map((c) => c.trim());
      let html = "<table><thead><tr>" + cells(line).map((c) => "<th>" + inlineMd(c) + "</th>").join("") + "</tr></thead><tbody>";
      i += 2;
      while (i < lines.length && /^\s*\|.*\|\s*$/.test(lines[i])) {
        html += "<tr>" + cells(lines[i]).map((c) => "<td>" + inlineMd(c) + "</td>").join("") + "</tr>";
        i++;
      }
      out.push(html + "</tbody></table>");
      continue;
    }
    const item = line.match(/^(\s*)([-*]|\d+[.)])\s+(.*)$/);
    if (item) {
      flush();
      const ordered = /\d/.test(item[2]);
      const items = [];
      while (i < lines.length) {
        const m = lines[i].match(/^(\s*)([-*]|\d+[.)])\s+(.*)$/);
        if (m) { items.push(m[3]); i++; continue; }
        if (lines[i].trim() && /^\s{2,}/.test(lines[i]) && items.length) { items[items.length - 1] += " " + lines[i].trim(); i++; continue; }
        break;
      }
      const tag = ordered ? "ol" : "ul";
      out.push("<" + tag + ">" + items.map((t) => "<li>" + inlineMd(t) + "</li>").join("") + "</" + tag + ">");
      continue;
    }
    if (!line.trim()) { flush(); i++; continue; }
    para.push(line.trim());
    i++;
  }
  flush();
  return out.join("\n");
}

document.addEventListener("click", (e) => {
  if (e.target.classList && e.target.classList.contains("copy")) {
    const code = e.target.parentElement.querySelector("code").textContent;
    navigator.clipboard.writeText(code).then(() => {
      e.target.textContent = "Copied";
      setTimeout(() => { e.target.textContent = "Copy"; }, 1500);
    });
  }
});

// ---------------------------------------------------------------- theme

function applyTheme(theme) {
  document.documentElement.dataset.theme = theme;
  try { localStorage.setItem("dryrun-theme", theme); } catch (e) { /* private mode */ }
  const label = theme === "light" ? "Dark theme" : "Light theme";
  for (const id of ["themeHome", "themeRoom", "themeResult"]) $(id).textContent = label;
}
function toggleTheme() {
  applyTheme(document.documentElement.dataset.theme === "light" ? "dark" : "light");
}
let savedTheme = "dark";
try { savedTheme = localStorage.getItem("dryrun-theme") || "dark"; } catch (e) { /* private mode */ }
applyTheme(savedTheme);
$("themeHome").onclick = toggleTheme;
$("themeRoom").onclick = toggleTheme;
$("themeResult").onclick = toggleTheme;

// ---------------------------------------------------------------- dashboard

let env = null;

function showOnly(id) {
  for (const view of ["home", "room", "result"]) $(view).hidden = view !== id;
  window.scrollTo(0, 0);
}

/** Per-problem progress worked out from the session list. */
function progressFor(problem) {
  const mine = env.sessions.filter((s) => s.problemId === problem.id);
  const finished = mine.filter((s) => s.finishedAt);
  const open = mine.find((s) => !s.finishedAt);
  const best = finished.reduce((m, s) => Math.max(m, s.score ?? 0), -1);
  const solved = finished.some((s) => s.solved);
  const state = solved ? "solved" : open ? "progress" : finished.length ? "attempted" : "new";
  return { attempts: mine.length, finished, open, best, solved, state, last: finished[0] };
}

const STATE_LABEL = {
  solved: ["✓", "Solved"],
  progress: ["▶", "In progress"],
  attempted: ["◐", "Attempted"],
  new: ["○", "Not started"],
};

async function showHome() {
  stopClock();
  session = null;
  showOnly("home");
  history.replaceState(null, "", "#");
  env = await api("GET", "state");
  $("envStatus").innerHTML = "Java " + esc(env.java) + ". " + (env.claude.available
    ? "Assistant: " + esc(env.claude.version) + "."
    : "<b>Assistant unavailable.</b> " + esc(env.claude.problem || "") + " You can still practise without chat.");

  const rank = { Easy: 0, Medium: 1, Hard: 2 };
  env.problems.sort((a, b) => (rank[a.difficulty] ?? 9) - (rank[b.difficulty] ?? 9));
  const progress = new Map(env.problems.map((p) => [p.id, progressFor(p)]));
  const all = [...progress.values()];
  const solved = all.filter((x) => x.solved).length;
  const untouched = all.filter((x) => x.state === "new").length;
  const attempted = all.length - solved - untouched;
  const scored = all.filter((x) => x.best >= 0);
  const average = scored.length ? Math.round(scored.reduce((sum, x) => sum + x.best, 0) / scored.length) : null;
  const finishedSessions = env.sessions.filter((s) => s.finishedAt);
  const practiceMs = finishedSessions.reduce((sum, s) => sum + (s.finishedAt - s.startedAt), 0);
  const stat = (value, label) => "<div><dd>" + value + "</dd><dt>" + label + "</dt></div>";
  $("stats").innerHTML =
    stat(solved + "<small> of " + all.length + "</small>", "Solved") +
    stat(attempted, "Attempted, not yet solved") +
    stat(untouched, "Not started") +
    stat(average === null ? "–" : average + "<small> / 100</small>", "Average of best scores") +
    stat(finishedSessions.length, "Finished sessions") +
    stat(Math.round(practiceMs / 60000) + "<small> min</small>", "Time practised");

  const categories = [...new Set(env.problems.map((p) => p.category || "Other"))];
  $("problemList").innerHTML = categories.map((category) => {
    const list = env.problems.filter((p) => (p.category || "Other") === category);
    const done = list.filter((p) => progress.get(p.id).solved).length;
    return '<div class="category"><h3>' + esc(category) + '<span class="count">' + done + " of " + list.length + " solved</span></h3>" +
      '<div class="rows">' + list.map((p) => {
        const g = progress.get(p.id);
        const [icon, label] = STATE_LABEL[g.state];
        const facts = [];
        if (g.best >= 0) facts.push("best " + g.best + " / 100");
        if (g.attempts) facts.push(g.attempts + (g.attempts === 1 ? " attempt" : " attempts"));
        return '<div class="row"><div><h3>' + esc(p.title) + '<span class="level">' + esc(p.difficulty) + ", " + p.minutes + " min</span></h3>" +
          '<p class="state ' + g.state + '"><span aria-hidden="true">' + icon + "</span> " + label + (facts.length ? '<span class="facts-inline">' + facts.join(", ") + "</span>" : "") + "</p>" +
          "<p>" + esc(p.summary) + '</p></div><div class="actions">' +
          (g.open ? '<button class="ghost" type="button" data-open="' + esc(g.open.id) + '">Resume</button>' : "") +
          (g.last ? '<button class="ghost" type="button" data-result="' + esc(g.last.id) + '">Last result</button>' : "") +
          '<button class="primary" type="button" data-start="' + esc(p.id) + '">' + (g.attempts ? "Try again" : "Start the clock") + "</button></div></div>";
      }).join("") + "</div></div>";
  }).join("");

  $("sessionList").innerHTML = env.sessions.length ? env.sessions.map((s) => {
    const passed = s.phasesPassed || {};
    let dots = "";
    for (let n = 1; n <= s.phaseCount; n++) dots += '<i class="' + (passed[n] !== undefined ? "on" : "") + '" title="Phase ' + n + (passed[n] !== undefined ? " passed" : " not passed") + '"></i>';
    const when = new Date(s.startedAt).toLocaleString([], { dateStyle: "medium", timeStyle: "short" });
    const state = s.finishedAt ? "finished in " + clockText(s.finishedAt - s.startedAt) : "in progress";
    return '<div class="row"><div><h3>' + esc(s.title) + (s.finishedAt ? '<span class="level">' + s.score + " / 100</span>" : "") +
      '</h3><p class="meta"><span class="dots">' + dots + "</span>" + esc(when) + ", " + state + '</p></div><div class="actions">' +
      (s.finishedAt ? '<button class="ghost" type="button" data-result="' + esc(s.id) + '">Result</button>'
        : '<button class="ghost" type="button" data-open="' + esc(s.id) + '">Resume</button>') + "</div></div>";
  }).join("") : '<div class="empty-row">No sessions yet. Start a problem above; everything you do is saved in the sessions folder.</div>';
}

$("home").addEventListener("click", async (e) => {
  const data = e.target.dataset || {};
  try {
    if (data.start) {
      e.target.disabled = true;
      enterRoom(await api("POST", "sessions", { problemId: data.start }));
    } else if (data.open) {
      enterRoom(await api("GET", "sessions/" + data.open));
    } else if (data.result) {
      showResult(await api("GET", "sessions/" + data.result));
    }
  } catch (err) {
    toast(err.message, "bad");
    if (data.start) e.target.disabled = false;
  }
});

// ---------------------------------------------------------------- room state

let session = null;      // latest view from the server
let files = {};          // path -> { doc, saved, readOnly }
let openTabs = [];
let activePath = null;
let editor = null;
let clockTimer = null;
let clockSkew = 0;       // server time minus browser time
let busyRun = false;
let busyChat = false;

function isFinished() { return session && session.finishedAt; }
function elapsedMs() {
  const end = isFinished() ? session.finishedAt : Date.now() + clockSkew;
  return end - session.startedAt;
}
function isDirty(path) {
  const f = files[path];
  return f && f.doc && f.doc.getValue() !== f.saved;
}

function enterRoom(view) {
  session = view;
  clockSkew = view.now - Date.now();
  files = {};
  openTabs = [];
  activePath = null;
  for (const f of view.files) files[f.path] = { doc: null, saved: null, readOnly: f.readOnly };
  showOnly("room");
  history.replaceState(null, "", "#s/" + view.id);
  $("roomTitle").textContent = view.title;
  $("output").innerHTML = '<span class="dim">Nothing has run yet. Ctrl+S saves and runs; Ctrl+Enter runs.</span>\n';
  ensureEditor();
  renderRunTargets();
  renderTree();
  renderTabs();
  renderPhases();
  renderBrief();
  renderChat();
  renderStrip();
  renderFinishedState();
  if (editor) setTimeout(() => editor.refresh(), 0);
  showSide("brief");
  startClock();
  if (files["Main.java"]) openFile("Main.java");
}

function ensureEditor() {
  if (editor) return;
  editor = CodeMirror($("editorWrap"), {
    mode: "text/x-java",
    theme: "dryrun",
    lineNumbers: true,
    indentUnit: 4,
    tabSize: 4,
    indentWithTabs: false,
    matchBrackets: true,
    autoCloseBrackets: true,
    styleActiveLine: true,
    extraKeys: {
      Tab: (cm) => cm.somethingSelected() ? cm.indentSelection("add") : cm.replaceSelection("    ", "end"),
      "Ctrl-/": "toggleComment",
      "Cmd-/": "toggleComment",
    },
  });
  editor.getWrapperElement().style.display = "none";
  editor.on("change", () => { renderTree(); renderTabs(); });
}

// ---------------------------------------------------------------- files, tabs, editor

function renderTree() {
  const paths = Object.keys(files).sort();
  const groups = [
    ["Source", paths.filter((p) => !p.includes("/") && !files[p].readOnly)],
    ["Tests and generators", paths.filter((p) => !p.includes("/") && files[p].readOnly)],
    ["Data", paths.filter((p) => p.includes("/"))],
  ];
  let html = "";
  for (const [name, list] of groups) {
    if (!list.length) continue;
    html += '<li class="group">' + name + "</li>";
    for (const p of list) {
      html += '<li><button type="button" data-path="' + esc(p) + '" class="' + (p === activePath ? "active" : "") + '">' +
        (isDirty(p) ? '<span class="dirty" title="Unsaved changes"></span>' : "") + "<span>" + esc(p) + "</span>" +
        (files[p].readOnly ? '<span class="lock">read-only</span>' : "") + "</button></li>";
    }
  }
  $("fileTree").innerHTML = html;
}
$("fileTree").addEventListener("click", (e) => {
  const btn = e.target.closest("button[data-path]");
  if (btn) openFile(btn.dataset.path);
});

function renderTabs() {
  $("tabs").innerHTML = openTabs.map((p) =>
    '<button class="tab' + (p === activePath ? " active" : "") + '" type="button" data-path="' + esc(p) + '">' +
    (isDirty(p) ? '<span class="dirty" title="Unsaved changes"></span>' : "") + "<span>" + esc(p) + "</span>" +
    (files[p].readOnly ? '<span class="ro">read-only</span>' : "") + '<span class="x" data-close="' + esc(p) + '" title="Close">×</span></button>').join("");
  const active = $("tabs").querySelector(".tab.active");
  if (active) active.scrollIntoView({ block: "nearest", inline: "nearest" });
}
$("tabs").addEventListener("wheel", (e) => {
  if (e.deltaY && !e.deltaX) { $("tabs").scrollLeft += e.deltaY; e.preventDefault(); }
}, { passive: false });
$("tabs").addEventListener("click", (e) => {
  const close = e.target.dataset && e.target.dataset.close;
  if (close) { closeTab(close); return; }
  const btn = e.target.closest("button[data-path]");
  if (btn) openFile(btn.dataset.path);
});

async function openFile(path) {
  const f = files[path];
  if (!f) return;
  try {
    if (!f.doc) {
      const data = await api("GET", "sessions/" + session.id + "/file?path=" + encodeURIComponent(path));
      f.saved = data.content;
      f.doc = CodeMirror.Doc(data.content, path.endsWith(".java") ? "text/x-java" : "text/plain");
    }
  } catch (err) { toast(err.message, "bad"); return; }
  if (!openTabs.includes(path)) openTabs.push(path);
  activePath = path;
  $("editorEmpty").hidden = true;
  editor.getWrapperElement().style.display = "";
  editor.swapDoc(f.doc);
  editor.setOption("readOnly", f.readOnly || !!isFinished());
  editor.refresh();
  editor.focus();
  renderTree();
  renderTabs();
}

function closeTab(path) {
  if (isDirty(path)) { toast("Save " + path + " before closing it (Ctrl+S).", "bad"); return; }
  openTabs = openTabs.filter((p) => p !== path);
  if (activePath === path) {
    activePath = null;
    if (openTabs.length) { openFile(openTabs[openTabs.length - 1]); return; }
    editor.getWrapperElement().style.display = "none";
    $("editorEmpty").hidden = false;
  }
  renderTree();
  renderTabs();
}

async function saveAll() {
  for (const path of Object.keys(files)) {
    if (!isDirty(path)) continue;
    const content = files[path].doc.getValue();
    await api("PUT", "sessions/" + session.id + "/file", { path, content });
    files[path].saved = content;
  }
  renderTree();
  renderTabs();
}

$("newFileBtn").onclick = async () => {
  if (isFinished()) return;
  const name = await ask({ title: "New file", text: "Name it like Helper.java. Allowed endings: .java, .txt, .md, .csv.", ok: "Create", input: "" });
  if (!name) return;
  try {
    const data = await api("POST", "sessions/" + session.id + "/files", { path: name });
    for (const f of data.files) if (!files[f.path]) files[f.path] = { doc: null, saved: null, readOnly: f.readOnly };
    openFile(name);
  } catch (err) { toast(err.message, "bad"); }
};

$("resetFileBtn").onclick = async () => {
  if (isFinished() || !activePath) return;
  const path = activePath;
  if (files[path].readOnly) { toast(path + " is read-only and already original.", ""); return; }
  const yes = await ask({ title: "Restore " + path + "?", text: "Your changes to this file are replaced with the original from the start of the session.", ok: "Restore" });
  if (!yes) return;
  try {
    const data = await api("POST", "sessions/" + session.id + "/reset-file", { path });
    files[path].saved = data.content;
    files[path].doc.setValue(data.content);
    renderTree(); renderTabs();
  } catch (err) { toast(err.message, "bad"); }
};

// ---------------------------------------------------------------- phases, brief, clock, strip

function renderPhases() {
  const passed = session.phasesPassed || {};
  $("phaseChips").innerHTML = session.phases.map((p) => {
    const done = passed[p.id] !== undefined;
    const state = done ? "passed" : p.id <= session.unlocked ? "open" : "locked";
    const tip = done ? "Passed at " + clockText(passed[p.id]) : state === "open" ? "Open" : "Opens when the phase before it passes";
    return '<li class="' + state + '" title="' + tip + '"><span class="n">' + p.id + "</span>" + esc(p.title) + (done ? " ✓" : "") + "</li>";
  }).join("");
}

function renderRunTargets() {
  const options = session.phases.map((p) => '<option value="phase' + p.id + '">Phase ' + p.id + " tests</option>");
  options.push('<option value="all">All tests</option>', '<option value="main">Main (demo)</option>');
  const previous = $("runTarget").value;
  $("runTarget").innerHTML = options.join("");
  $("runTarget").value = previous && previous !== "" ? previous : "phase1";
  if (!$("runTarget").value) $("runTarget").value = "phase1";
}

function renderBrief() {
  let html = "";
  session.brief.forEach((part, index) => {
    if (part !== null) { html += renderMd(part); return; }
    if (index === session.unlocked + 1 || session.brief[index - 1] !== null) {
      html += '<div class="locked-phase">Phase ' + index + " opens when the phase " + (index - 1) + " tests pass." +
        (isFinished() ? "" : '<br><button class="ghost small" type="button" id="skipBtn">Stuck? Open phase ' + index + " anyway</button>") + "</div>";
    }
  });
  $("brief").innerHTML = html;
  const skip = $("skipBtn");
  if (skip) skip.onclick = async () => {
    const yes = await ask({ title: "Open the next phase?", text: "In a real round the interviewer may move you on. The skip is recorded in your review.", ok: "Open it" });
    if (!yes) return;
    try {
      const view = await api("POST", "sessions/" + session.id + "/unlock", {});
      Object.assign(session, { unlocked: view.unlocked, brief: view.brief, events: view.events });
      renderBrief(); renderPhases(); renderStrip();
    } catch (err) { toast(err.message, "bad"); }
  };
}

function startClock() {
  stopClock();
  tick();
  clockTimer = setInterval(tick, 1000);
}
function stopClock() { clearInterval(clockTimer); clockTimer = null; }
function tick() {
  if (!session) return;
  const limit = session.minutes * 60000;
  const used = elapsedMs();
  const el = $("clock");
  if (isFinished()) {
    el.textContent = clockText(used);
    el.className = "clock done";
    el.title = "Time used";
  } else if (used <= limit) {
    el.textContent = clockText(limit - used);
    el.className = "clock";
    el.title = "Time left";
  } else {
    el.textContent = "+" + clockText(used - limit);
    el.className = "clock over";
    el.title = "Over time";
  }
  const now = $("stripNow");
  if (now && !isFinished()) now.style.left = (100 * used / stripSpan()) + "%";
}

function stripSpan() {
  return Math.max(session.minutes * 60000, elapsedMs() * 1.02);
}

/** The session drawn on a time axis: runs as bars, prompts as dots, phase passes as flags. */
function renderStrip(target) {
  const span = stripSpan();
  const pos = (t) => (100 * Math.min(t, span) / span).toFixed(2) + "%";
  let html = "";
  for (let m = 0; m * 60000 <= span; m += 10) html += '<div class="tick" style="left:' + pos(m * 60000) + '"><span>' + m + "m</span></div>";
  if (span > session.minutes * 60000) html += '<div class="limit" style="left:' + pos(session.minutes * 60000) + '" title="Time limit"></div>';
  for (const e of session.events || []) {
    if (e.type === "run") {
      const kind = e.compileError ? "broken" : e.failed > 0 ? "fail" : "";
      const label = e.compileError ? "did not compile" : e.passed + " passed, " + e.failed + " failed";
      html += '<div class="run ' + kind + '" style="left:' + pos(e.t) + '" title="' + clockText(e.t) + " run " + esc(e.target) + ": " + label + '"></div>';
    } else if (e.type === "chat") {
      html += '<div class="prompt" style="left:' + pos(e.t) + '" title="' + clockText(e.t) + ' prompt"></div>';
    } else if (e.type === "phasePassed") {
      html += '<div class="flag" style="left:' + pos(e.t) + '" title="Phase ' + e.phase + " passed at " + clockText(e.t) + '">P' + e.phase + "</div>";
    }
  }
  if (!isFinished()) html += '<div class="now" id="stripNow" style="left:' + pos(elapsedMs()) + '"></div>';
  (target || $("stripTrack")).innerHTML = html;
}

// ---------------------------------------------------------------- run

function appendOutput(header, text) {
  const out = $("output");
  if (out.querySelector(".dim") && !out.querySelector(".head")) out.innerHTML = "";
  const head = document.createElement("span");
  head.className = "head";
  head.textContent = "── " + header + " ──";
  out.appendChild(head);
  for (const line of text.split("\n")) {
    const span = document.createElement("span");
    if (/^PASS /.test(line)) span.className = "pass";
    else if (/^(FAIL |TIMEOUT |Compilation failed|Stopped:)/.test(line) || /error:/.test(line)) span.className = "fail";
    else if (/^RESULT /.test(line)) span.className = "sum " + (/failed=0 /.test(line) ? "pass" : "fail");
    span.textContent = line + "\n";
    out.appendChild(span);
  }
  out.scrollTop = out.scrollHeight;
}

async function run() {
  if (busyRun || !session) return;
  if (isFinished()) { toast("This session is finished. Start the problem again to keep practising.", "bad"); return; }
  busyRun = true;
  const btn = $("runBtn");
  btn.disabled = true;
  btn.textContent = "Running";
  const target = $("runTarget").value;
  const label = $("runTarget").selectedOptions[0].textContent;
  try {
    await saveAll();
    const r = await api("POST", "sessions/" + session.id + "/run", { target });
    appendOutput(label + " at " + clockText(elapsedMs()) + " (" + (r.durationMs / 1000).toFixed(1) + " s)", r.output);
    session.phasesPassed = r.phasesPassed;
    session.unlocked = r.unlocked;
    session.brief = r.brief;
    session.events = r.events;
    renderPhases(); renderBrief(); renderStrip();
    for (const f of Object.values(files)) if (f.readOnly && f.doc && f.saved !== f.doc.getValue()) f.doc.setValue(f.saved);
    if (r.newlyPassed && r.newlyPassed.length) {
      const n = r.newlyPassed[r.newlyPassed.length - 1];
      const next = session.phases.find((p) => p.id === n + 1);
      toast("Phase " + n + " passed at " + clockText(elapsedMs()) + "." + (next ? " The phase " + next.id + " brief is open." : " All phases passed."), "good");
      if (next) { $("runTarget").value = "phase" + next.id; showSide("brief"); }
    }
  } catch (err) {
    toast(err.message, "bad");
  } finally {
    busyRun = false;
    btn.disabled = false;
    btn.textContent = "Run";
  }
}
$("runBtn").onclick = run;
$("clearOut").onclick = () => { $("output").innerHTML = ""; };

document.addEventListener("keydown", (e) => {
  if ($("room").hidden || !$("modal").hidden) return;
  const mod = e.ctrlKey || e.metaKey;
  if (mod && (e.key === "s" || e.key === "S")) { e.preventDefault(); run(); }
  else if (mod && e.key === "Enter" && document.activeElement !== $("chatInput")) { e.preventDefault(); run(); }
});

// ---------------------------------------------------------------- side panel

function showSide(name) {
  for (const tab of $("sideTabs").querySelectorAll(".tab")) tab.classList.toggle("active", tab.dataset.side === name);
  for (const id of ["brief", "chat"]) $("side-" + id).hidden = id !== name;
  if (name === "chat") { $("chatLog").scrollTop = $("chatLog").scrollHeight; if (!isFinished()) $("chatInput").focus(); }
}
$("sideTabs").addEventListener("click", (e) => {
  if (e.target.dataset.side) showSide(e.target.dataset.side);
});

// ---------------------------------------------------------------- assistant chat

const MODE_HINTS = {
  interview: "Interview mode: the assistant will not volunteer bugs or whole solutions. It does what you specify. This is how several candidates describe the real round.",
  helpful: "Helpful mode: an unrestricted assistant. Easier than the real round is reported to be.",
};
function renderModeHint() { $("modeHint").textContent = MODE_HINTS[$("chatMode").value]; }
$("chatMode").onchange = renderModeHint;
renderModeHint();

function messageHtml(turn) {
  const who = turn.role === "user" ? "You" : turn.role === "error" ? "Problem" : "Assistant" + (turn.model ? " (" + turn.model + ")" : "");
  const body = turn.role === "assistant" ? '<div class="body md">' + renderMd(turn.text) + "</div>" : '<div class="body">' + esc(turn.text) + "</div>";
  return '<div class="msg ' + turn.role + '"><div class="who">' + who + '<span class="at">' + clockText(turn.t || 0) + "</span></div>" + body + "</div>";
}

function renderChat() {
  const log = $("chatLog");
  let html = "";
  if (env && !env.claude.available) html += '<div class="notice">' + esc(env.claude.problem || "The assistant is unavailable.") + " Install Claude Code, run <code>claude</code> once in a terminal to sign in, then restart Dryrun.</div>";
  if (!session.chat.length) {
    html += '<p class="empty-chat">Nothing asked yet. Read the code first, decide what you want, then ask for that specific thing. Say your plan aloud before you send it.</p>';
  }
  html += session.chat.map(messageHtml).join("");
  log.innerHTML = html;
  log.scrollTop = log.scrollHeight;
  const off = !!isFinished();
  $("chatInput").disabled = off;
  $("chatSend").disabled = off;
}

async function sendChat(e) {
  e.preventDefault();
  if (busyChat || isFinished()) return;
  const input = $("chatInput");
  const message = input.value.trim();
  if (!message) return;
  busyChat = true;
  $("chatSend").disabled = true;
  const model = $("chatModel").value;
  try {
    await saveAll();
  } catch (err) {
    toast(err.message, "bad");
    busyChat = false;
    $("chatSend").disabled = false;
    return;
  }
  input.value = "";
  const t = elapsedMs();
  session.chat.push({ role: "user", text: message, t });
  renderChat();
  const log = $("chatLog");
  const holder = document.createElement("div");
  holder.className = "msg assistant";
  holder.innerHTML = '<div class="who">Assistant (' + esc(model) + ')</div><div class="body md"><span class="thinking">Thinking…</span></div>';
  log.appendChild(holder);
  log.scrollTop = log.scrollHeight;
  const body = holder.querySelector(".body");
  let text = "";
  let scheduled = false;
  const paint = () => {
    scheduled = false;
    const stick = log.scrollHeight - log.scrollTop - log.clientHeight < 80;
    body.innerHTML = renderMd(text);
    if (stick) log.scrollTop = log.scrollHeight;
  };
  try {
    const done = await stream("sessions/" + session.id + "/chat", { message, model, mode: $("chatMode").value }, (delta) => {
      text += delta;
      if (!scheduled) { scheduled = true; requestAnimationFrame(paint); }
    });
    session.chat.push({ role: "assistant", text, model, t: elapsedMs() });
    if (done.events) session.events = done.events;
  } catch (err) {
    session.chat.push({ role: "error", text: err.message, t: elapsedMs() });
    if (err.events) session.events = err.events;
  }
  renderChat();
  renderStrip();
  busyChat = false;
  $("chatSend").disabled = !!isFinished();
  input.focus();
}
$("chatForm").addEventListener("submit", sendChat);
$("chatInput").addEventListener("keydown", (e) => {
  if (e.key === "Enter" && !e.shiftKey && !e.isComposing) { e.preventDefault(); $("chatForm").requestSubmit(); }
});

// ---------------------------------------------------------------- finish

$("finishBtn").onclick = async () => {
  if (isFinished()) { showResult(session); return; }
  const yes = await ask({ title: "Finish this session?", text: "The clock stops, all tests run one last time and the files become read-only. Your result opens next. You cannot resume afterwards.", ok: "Finish" });
  if (!yes) return;
  try {
    await saveAll();
    $("finishBtn").disabled = true;
    $("finishBtn").textContent = "Running all tests";
    const view = await api("POST", "sessions/" + session.id + "/finish", {});
    showResult(view);
  } catch (err) { toast(err.message, "bad"); }
  $("finishBtn").disabled = false;
};

function renderFinishedState() {
  const done = !!isFinished();
  $("finishBtn").textContent = done ? "Result" : "Finish";
  $("runBtn").disabled = done;
}

// ---------------------------------------------------------------- result page

function showResultTab(name) {
  for (const tab of $("resultTabs").querySelectorAll(".tab")) tab.classList.toggle("active", tab.dataset.rtab === name);
  for (const id of ["phases", "walkthrough", "feedback"]) $("rtab-" + id).hidden = id !== name;
}
$("resultTabs").addEventListener("click", (e) => { if (e.target.dataset.rtab) showResultTab(e.target.dataset.rtab); });
$("resultHome").onclick = () => showHome().catch((err) => toast(err.message, "bad"));
$("resultOpen").onclick = () => { if (session) enterRoom(session); };
$("resultAgain").onclick = async () => {
  try { enterRoom(await api("POST", "sessions", { problemId: session.problemId })); } catch (err) { toast(err.message, "bad"); }
};

function showResult(view) {
  stopClock();
  session = view;
  clockSkew = view.now - Date.now();
  showOnly("result");
  history.replaceState(null, "", "#r/" + view.id);
  const r = view.report;
  const score = r.score;
  const limit = view.minutes * 60000;
  const when = new Date(view.startedAt).toLocaleString([], { dateStyle: "medium", timeStyle: "short" });
  $("resultTitle").textContent = view.title;

  $("scoreline").innerHTML =
    '<div class="score"><span class="num">' + score.total + '</span><span class="of">/ 100</span></div>' +
    '<div class="score-text"><h1>' + esc(score.band) + (score.solved ? ", all phases solved" : "") + "</h1>" +
    "<p>" + esc(view.category || "") + ", " + esc(view.difficulty) + ". " + esc(when) + ". Time used " + clockText(r.totalMs) + " of " + view.minutes + ":00" + (r.totalMs > limit ? " (over)" : "") + ".</p>" +
    '<p class="hint">The score counts what this tool can observe: tests, time and how often you ran your code. It cannot hear you, so communication is not scored.</p></div>';

  renderStrip($("resultStrip"));

  // --- tab 1: result by phase
  const passed = view.phasesPassed || {};
  const events = view.events || [];
  let from = 0;
  let html = "";
  for (const p of view.phases) {
    const passedAt = passed[p.id];
    const to = passedAt !== undefined ? passedAt : r.totalMs;
    const inWindow = (e) => e.t >= from && e.t <= to;
    const runs = events.filter((e) => e.type === "run" && inWindow(e)).length;
    const prompts = events.filter((e) => e.type === "chat" && inWindow(e)).length;
    const section = (view.finalResults || []).find((s) => s.name === p.tests) || { passed: 0, failed: 0, tests: [] };
    const ok = section.passed > 0 && section.failed === 0;
    html += '<section class="phase-result"><h2>Phase ' + p.id + ": " + esc(p.title) +
      '<span class="verdict ' + (ok ? "good" : "bad") + '">' + (ok ? "✓ Passed" : "✗ Not passed") + "</span></h2>" +
      "<p>" + (passedAt !== undefined
        ? "First passed at " + clockText(passedAt) + ", after " + clockText(passedAt - from) + " on this phase."
        : "Never passed during the session. Worked on from " + clockText(from) + " to the end.") +
      " " + runs + (runs === 1 ? " run" : " runs") + " and " + prompts + (prompts === 1 ? " prompt" : " prompts") + " in that time. Final run: " +
      section.passed + " of " + (section.passed + section.failed) + " tests pass.</p>";
    if (section.tests && section.tests.length) {
      html += '<table class="tests"><tbody>' + section.tests.map((t) =>
        '<tr><td class="' + (t.status === "pass" ? "good" : "bad") + '">' + (t.status === "pass" ? "✓ pass" : t.status === "timeout" ? "✗ timeout" : "✗ fail") + "</td><td><code>" + esc(t.name) +
        "</code></td><td>" + esc(t.status === "pass" ? "" : t.detail) + "</td></tr>").join("") + "</tbody></table>";
    } else if (view.finalCompileError) {
      html += '<p class="bad">The final code did not compile, so no test ran.</p>';
    }
    html += "</section>";
    if (passedAt !== undefined) from = passedAt;
  }
  const fact = (label, value, tone) => "<dt>" + label + '</dt><dd class="' + (tone || "") + '">' + value + "</dd>";
  html += '<section class="phase-result"><h2>How you worked</h2><p>Interviewers look for a steady loop: prompt, read the reply, run, repeat. On the strip, that is a dot followed closely by a bar.</p><dl class="facts">' +
    fact("First run", r.firstRunMs < 0 ? "never" : "at " + clockText(r.firstRunMs)) +
    fact("First prompt", r.firstPromptMs < 0 ? "never" : "at " + clockText(r.firstPromptMs)) +
    fact("Runs", r.runs + (r.compileErrors ? " (" + r.compileErrors + " did not compile)" : "")) +
    fact("Prompts", r.prompts) +
    fact("Longest stretch without a run", clockText(r.longestGapWithoutRunMs), r.longestGapWithoutRunMs > 8 * 60000 ? "bad" : "") +
    fact("Most prompts in a row without a run", r.longestPromptStreak, r.longestPromptStreak > 2 ? "bad" : "") +
    (r.phasesSkipped ? fact("Phases opened without passing", r.phasesSkipped, "bad") : "") + "</dl></section>";
  html += '<section class="phase-result"><h2>How the score is made</h2><table class="tests"><tbody>' +
    score.parts.map((part) => "<tr><td>" + esc(part.label) + '</td><td class="points">' + part.points + " / " + part.max + "</td><td>" + esc(part.note) + "</td></tr>").join("") +
    '<tr><td><strong>Total</strong></td><td class="points"><strong>' + score.total + " / 100</strong></td><td>85 and above with every phase passing is Strong; 70 Solid; 50 Partial</td></tr></tbody></table></section>";
  $("rtab-phases").innerHTML = html;

  // --- tab 2: what a strong round looks like
  $("rtab-walkthrough").innerHTML = view.walkthrough
    ? '<div class="md wide">' + renderMd(view.walkthrough) + "</div>"
    : '<p class="hint">No walkthrough has been written for this problem.</p>';

  // --- tab 3: follow-ups, notes, written feedback
  const questions = (view.followUps || []).map((q) => "<li>" + esc(q) + "</li>").join("");
  const canAsk = env && env.claude.available;
  $("rtab-feedback").innerHTML =
    '<div class="md wide"><h2 class="first">Answer these aloud</h2><p>An interviewer would ask them now. Give each one a minute, out loud, before you read the model answers at the end of the walkthrough.</p><ol>' + questions + "</ol>" +
    "<h2>Your notes</h2><p>What did the assistant get wrong? What would you do differently? Your notes are given to the reviewer.</p></div>" +
    '<textarea id="notes" placeholder="Saved as you type."></textarea>' +
    '<div class="md wide"><h2>Written feedback</h2><p>A reviewer model reads your timeline, prompts and final code against the grading notes for this problem, then scores problem solving, control of the assistant, verification, code quality and communication.</p></div>' +
    '<div class="feedback-controls"><select id="feedbackModel"><option value="sonnet">Sonnet</option><option value="opus">Opus</option></select>' +
    '<button class="primary small" id="feedbackBtn" type="button"' + (canAsk ? "" : " disabled") + ">" + (view.feedback ? "Ask again" : "Get written feedback") + "</button>" +
    (canAsk ? "" : '<span class="hint">Needs the Claude Code CLI.</span>') + "</div>" +
    '<div class="md wide" id="feedback">' + (view.feedback ? renderMd(view.feedback) : "") + "</div>";

  const notes = $("notes");
  notes.value = view.notes || "";
  let saveTimer = null;
  notes.oninput = () => {
    clearTimeout(saveTimer);
    saveTimer = setTimeout(() => {
      session.notes = notes.value;
      api("POST", "sessions/" + session.id + "/notes", { notes: notes.value }).catch((err) => toast(err.message, "bad"));
    }, 600);
  };
  $("feedbackBtn").onclick = async () => {
    const btn = $("feedbackBtn");
    btn.disabled = true;
    btn.textContent = "Reviewing";
    const box = $("feedback");
    box.innerHTML = '<p class="hint">Reading your session. This takes up to a minute.</p>';
    let text = "";
    try {
      await api("POST", "sessions/" + session.id + "/notes", { notes: notes.value });
      await stream("sessions/" + session.id + "/feedback", { model: $("feedbackModel").value }, (delta) => {
        text += delta;
        box.innerHTML = renderMd(text);
      });
      session.feedback = text;
    } catch (err) {
      box.innerHTML = '<div class="notice">' + esc(err.message) + "</div>";
    }
    btn.disabled = false;
    btn.textContent = "Ask again";
  };
  showResultTab("phases");
}

// ---------------------------------------------------------------- resizing

function dragResize(handle, onMove) {
  handle.addEventListener("pointerdown", (e) => {
    e.preventDefault();
    handle.setPointerCapture(e.pointerId);
    const move = (ev) => { onMove(ev); if (editor) editor.refresh(); };
    const up = () => { handle.removeEventListener("pointermove", move); handle.removeEventListener("pointerup", up); };
    handle.addEventListener("pointermove", move);
    handle.addEventListener("pointerup", up);
  });
}
dragResize($("rightSplit"), (e) => {
  const width = Math.min(Math.max(window.innerWidth - e.clientX, 300), window.innerWidth - 520);
  $("work").style.setProperty("--right-w", width + "px");
});
dragResize($("outSplit"), (e) => {
  const box = document.querySelector(".center").getBoundingClientRect();
  const height = Math.min(Math.max(box.bottom - e.clientY, 80), box.height - 140);
  document.querySelector(".center").style.setProperty("--out-h", height + "px");
});

// ---------------------------------------------------------------- navigation

$("backHome").onclick = async () => {
  try { if (session && !isFinished()) await saveAll(); } catch (err) { toast(err.message, "bad"); return; }
  showHome().catch((err) => toast(err.message, "bad"));
};

window.addEventListener("beforeunload", (e) => {
  if (session && !$("room").hidden && Object.keys(files).some(isDirty)) { e.preventDefault(); e.returnValue = ""; }
});

(async function boot() {
  try {
    env = await api("GET", "state");
    const match = location.hash.match(/^#(?:(s|r)\/)?([a-z0-9-]+)$/);
    const known = match && env.sessions.find((s) => s.id === match[2]);
    if (known) {
      const view = await api("GET", "sessions/" + known.id);
      if (view.finishedAt && match[1] !== "s") showResult(view); else enterRoom(view);
    } else {
      await showHome();
    }
  } catch (err) {
    document.body.innerHTML = '<p style="padding:40px;font:16px sans-serif">Dryrun could not reach its server. Start it with run.bat (or run.sh) and reload this page.<br><br>' + esc(err.message) + "</p>";
  }
})();
