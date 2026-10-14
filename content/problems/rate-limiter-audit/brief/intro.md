# Rate Limiter Audit

An API gateway lets each client make at most **limit** requests in any **window** of milliseconds. A customer says the gateway rejected requests it should have allowed, so the platform team wants to audit the access log.

A window of `w` ms that ends at time `t` covers every request after `t - w` and up to and including `t`: start excluded, end included.

You have 45 minutes for three phases. Start by reading the code; `Main` prints window counts for the sample log in `data/requests.csv`.

| File | What it holds |
| --- | --- |
| `Request` | One log line: client and time |
| `RequestLog` | Every request, oldest first |
| `LogParser` | Reads the log; times are in seconds with milliseconds, such as `2.006` |
| `RateAudit` | The audit. Most of your work is here. |
| `Datasets` | Builds the large logs for phase 3 |

Times are milliseconds since the log started. Tests are read-only. You can add your own files.
