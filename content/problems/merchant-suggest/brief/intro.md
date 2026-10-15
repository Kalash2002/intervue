# Merchant Suggest

In the payments app, the **Pay to** box suggests merchants while the customer types. Suggestions come from the merchant directory, which is reloaded from a feed every night.

You have 60 minutes for three phases. Start by reading the code; `Main` looks up a few merchants in `data/merchants.txt`.

| File | What it holds |
| --- | --- |
| `Merchant` | Data record |
| `Normalizer` | Makes names and typed text comparable |
| `MerchantDirectory` | All merchants, sorted by normalized name |
| `DirectoryLoader` | Reads the directory from a file |
| `Suggester` | The suggestions. Your phase 2 work is here. |
| `Datasets` | Builds the large directory for phase 3 |

Tests are read-only. You can add your own files.
