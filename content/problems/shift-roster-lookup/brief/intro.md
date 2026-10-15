# Shift Roster Lookup

A hospital app shows who is on duty in each ward. A **shift** covers a ward from its start minute up to, **but not including**, its end minute; minutes count from the start of the week. In one ward, shifts never overlap, but there can be gaps between them.

You have 45 minutes for three phases. Start by reading the code; `Main` prints lookups for the sample roster in `data/roster.csv`.

| File | What it holds |
| --- | --- |
| `Shift`, `Gap` | Data records |
| `Roster` | Every ward's shifts, sorted by start, with their start minutes |
| `RosterParser` | Reads the roster from CSV |
| `RosterLookup` | The questions the app asks. Most of your work is here. |
| `Datasets` | Builds the large rosters for phase 3 |

Tests are read-only. You can add your own files.
