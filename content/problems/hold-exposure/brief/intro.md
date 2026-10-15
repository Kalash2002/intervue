# Hold Exposure

When a card is authorised, the amount is **held** on the account until the payment settles or the hold expires. The risk team needs to know how much is held on an account at any moment.

A hold is active from its start minute up to, **but not including**, its end minute. Holds can be released early.

You have 60 minutes for three phases. Start by reading the code; `Main` prints exposure for the sample data in `data/holds.csv`.

| File | What it holds |
| --- | --- |
| `Hold`, `Peak` | Data records |
| `HoldBook` | Every hold, with early release |
| `HoldParser` | Reads holds from CSV |
| `ExposureCalculator` | The risk queries. Most of your work is here. |
| `Datasets` | Builds the large books for phase 3 |

Amounts are in paise. Tests are read-only. You can add your own files.
