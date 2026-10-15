## Level 1

Both questions start from the same place you found in phase 1: the last shift that starts at or before a minute. The next handover is the shift after it; the gaps are what lies between shifts as you walk forward from it.

## Level 2

Put the floor search in one private method and reuse it. For the next handover, take the floor index plus one, if it exists. For gaps, keep a "covered up to" minute starting at the range start, walk the shifts from the floor index, record a gap whenever a shift starts later than that minute, move it to the shift's end, stop at the first shift starting at or after the range end, and add a final gap if the range is not yet covered.

## Level 3

1. Write a helper that returns the index of the last shift starting at or before a minute, or -1.
2. For `nextHandover`, add one to that index and read the start minute if the index is inside the list.
3. For `uncovered`, reject an empty range first.
4. Walk forward from the helper's index (or 0), keeping the covered minute and cutting every gap to the range.
5. Treat an unknown ward as having no shifts, which gives one gap over the whole range.
