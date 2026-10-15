# Corridor Router

Our payments platform moves money through **corridors**: one-way links between nodes (currencies, banks, partners). Each corridor charges a fixed fee and can be switched off.

The router answers one question: *which corridors should this payment travel through?*

The project already has a working loader and a hop-count search. You have 60 minutes for three phases. Start by reading the code; `Main` runs a small demo on `data/corridors.txt`.

| File | What it holds |
| --- | --- |
| `Corridor`, `Route` | Data records |
| `Network` | The corridors, grouped by the node they leave |
| `NetworkLoader` | Reads a network from text |
| `RouteFinder` | The searches. Your work is here. |
| `Datasets` | Builds the large networks for phase 3 |

Tests are read-only. You can add your own files.
