# Duplicate Order Detector

A shop's checkout sometimes sends the same basket twice: a customer double-clicks **Pay**, or a mobile app retries after a timeout. Support wants a report of orders that repeat an earlier one so they can refund the extra charge.

Two orders are **the same basket** when the customer, the SKU and the amount are all equal. The order id and the minute differ.

You have 45 minutes for three phases. Start by reading the code; `Main` prints a report for the sample data in `data/orders.csv`.

| File | What it holds |
| --- | --- |
| `Order`, `SkuCount` | Data records |
| `OrderKey` | The part of an order that decides "same basket" |
| `OrderLog` | Every order, in the order it arrived |
| `OrderParser` | Reads orders from CSV |
| `DuplicateDetector` | The reports. Most of your work is here. |
| `Datasets` | Builds the large logs for phase 3 |

Amounts are in paise; minutes count from the start of the day. Tests are read-only. You can add your own files.
