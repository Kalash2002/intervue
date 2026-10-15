## Level 1
Think about the decoder. Looking at the encoded text, how does it know where one string stops and the next one starts?

## Level 2
A separator character fails as soon as a string contains that character. What could the decoder be told in advance instead, so it never has to look inside a string to find its end?

## Level 3
The decoder cannot trust the content, so it must be told how much to read. What could you write in front of each string for that, and how does the decoder know where that prefix ends?
