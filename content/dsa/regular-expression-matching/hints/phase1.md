## Level 1
Letting a starred piece take every letter it can fails Example 2, and so does letting it take as few as it can. A starred piece may also take no letter at all, as `a*` does in Example 1, and the match has to cover the whole text.

## Level 2
Match from the front. A plain letter or dot must match the next letter of the text. A starred piece either matches nothing and is skipped, or matches the next letter and stays in play for the one after. When both are possible, try both.

## Level 3
Whatever choices led there, what is left to decide is always whether a rest of the pattern matches a rest of the text, so there are only about n × m different questions.
