# Courier Load Balancer

Every morning a delivery hub splits the day's parcels between its couriers so that nobody carries far more than the others. The planner uses a simple, well-known rule:

1. Take the parcels **heaviest first**. Parcels of equal weight go in order of their id.
2. Give each parcel to the courier with the **lightest load so far**. When several couriers are equally light, the one listed **first** gets it.

You have 60 minutes for three phases. Start by reading the code; `Main` prints the plan for the sample parcels in `data/parcels.csv`.

| File | What it holds |
| --- | --- |
| `Parcel` | One parcel: id and weight in grams |
| `Plan` | Which courier carries which parcels, their loads, and parcels left over |
| `ParcelParser` | Reads parcels from CSV |
| `LoadBalancer` | The planner. Most of your work is here. |
| `Datasets` | Builds the large hubs for phase 3 |

Tests are read-only. You can add your own files.
