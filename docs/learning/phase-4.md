# Learning for Phase 4: Python language package and full port

Time (est.): 20 hours.

## Minimal concepts

| Concept | Why it matters in Intervue | Source |
|---|---|---|
| Reading Python: modules, functions, classes, dataclasses, type hints, list and dict comprehensions, generators, `with` | Reviewing AI-drafted ports and writing one yourself | The official Python tutorial, chapters 3 to 9; `dataclasses` and `typing` docs |
| Standard-library equivalents of Java collections and algorithms: `list`, `dict`, `set`, `collections.deque`, `heapq`, `bisect`, `functools.lru_cache`, `itertools` | Every port must be standard library only and idiomatic | Python standard library docs for those modules |
| Why pure-Python loops are 20 to 60 times slower than Java and C-backed calls are not | Limits and workloads must be redesigned per language, not translated | Benchmarks Game Python versus Java page; PLAN.md 4.2 |
| `subprocess.run(timeout=)`, `faulthandler.dump_traceback_later(exit=True)`, why threads cannot be killed, `spawn` on Windows | The per-test timeout mechanism of the Python kit | Python docs for `subprocess`, `faulthandler`, `threading`, `multiprocessing` |
| python-build-standalone: what it is, variants, hashes, licences | The downloaded toolchain | Astral's python-build-standalone docs |
| Timing noise and headroom: medians of repeated runs, 6x reference | Phase 3 tests that do not flake | PLAN.md 4.4 |

## Tutor prompt

```
You are my tutor for reading and reviewing Python. I am a backend engineer (Java, Spring Boot) who has never written Python, building Intervue, where every interview problem must have a Python variant (starter code, read-only tests, reference solution, deliberately slow solution) that runs standard-library only in a local runner with per-test timeouts. Most ports will be drafted by an AI; I must be able to read them critically and fix them. I have about 20 hours.

Teach me only: Python syntax and idioms I will meet in such code (modules, functions, classes, dataclasses, type hints, comprehensions, generators, with-statements, exceptions); the standard-library equivalents of Java's ArrayList, HashMap, HashSet, ArrayDeque, PriorityQueue, TreeMap-style sorted access, and memoisation; where pure-Python loops are slow and where C-backed functions are fast, with the consequences for test workloads and time limits; how to run a test in a child process with subprocess.run(timeout=) and a faulthandler backstop, and why threads cannot be killed; how multiprocessing differs on Windows; what python-build-standalone is and how I would verify a download; how to measure timing noise and set limits with headroom.

Method: show me small Java snippets I know and their idiomatic Python, two sentences of explanation, one common mistake an AI port makes (for example mutable default arguments, integer division, or sorting stability assumptions), then one question to check I understood before moving on. Keep answers short. At the end give me a 30-minute exercise: port one small Java class with a sorted sweep over intervals to Python by hand, and a checklist of what I should be able to spot in a port without notes.

Do not teach me web frameworks, data science libraries, async Python, or packaging with pip. Say when an API detail depends on the Python version.
```
