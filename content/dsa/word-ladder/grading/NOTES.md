# Grader notes: Word Ladder

Tests cover: the sample, a one-change answer, begin equal to end, an end word that is not allowed, an end word that is allowed but unreachable, a begin word that is itself in the list, a shortcut that must beat a longer route, one-letter words, and a large case of about 49,000 words of length 6 in which the end word is allowed but has no neighbour, so a correct search must explore the whole connected part of the list before answering -1.

The naive solution searches level by level but finds each word's neighbours by comparing it with every allowed word, which is quadratic in the list size. On the large test it exceeds the limit and 3x the limit. The reference groups words by the shape left when one letter is blanked, so finding neighbours costs the word length.
