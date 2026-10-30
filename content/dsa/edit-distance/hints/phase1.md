## Level 1
Comparing the two words position by position goes wrong as soon as one letter is missing: in Example 1 it counts 6, but deleting the l lines the rest up. And a replacement is one fix, where a deletion followed by an insertion is two.

## Level 2
Look at the last letters of the two words. If they are equal, keep them, and the question becomes the same one about both words without them. If they differ, the last fix touched one of them: it replaced the typed letter, deleted it, or inserted the wanted one, and each choice leaves the same question about shorter beginnings.

## Level 3
Every question that comes up pairs a beginning of `typed` with a beginning of `word`, so there are only about n × m different ones, and each answer needs only answers for beginnings one letter shorter.
