## Level 1

Only capital letters slip through, so the lower-casing step is the suspect. Read `SongKey.normalise` line by line and ask what each line does with the value it produces.

## Level 2

Java strings never change. Methods such as `toLowerCase`, `strip` and `replaceAll` return a new string, and the original stays as it was. A call whose result is not assigned does nothing, and the compiler does not complain about an ignored return value.

## Level 3

1. Open `SongKey.normalise` and find the call to `toLowerCase`.
2. Assign its result back to the variable, or chain it onto the line before.
3. Check that every other step in the method also keeps its result.
4. Run Phase 1 tests: the sample playlist should dedupe to five tracks.
