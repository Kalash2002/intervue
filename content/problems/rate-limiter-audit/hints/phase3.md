## Level 1

The first workload is one client with a very long history; the second is many clients with short ones. Ask what each workload repeats: work per request, or work per client.

## Level 2

For the noisy client, counting accepted requests from the start for every request is quadratic; a queue that drops expired times keeps each request to constant work on average. For many clients, the time goes into `RequestLog.forClient`, which scans the whole log once per client.

## Level 3

1. Make `rejections` keep a queue of accepted times and drop expired ones from the front.
2. Read `RequestLog` and the second test together and count how many log entries `forClient` visits in total.
3. Change `RequestLog` to keep a map from client to its list of times as requests are added.
4. Keep the out-of-order check and the client order that `clients()` already promises.
5. Run Phase 3 tests and compare each test's time with its limit.
