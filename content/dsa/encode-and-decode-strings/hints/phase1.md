## Level 1
Think about the decoder. Looking at the encoded text, how does it know where one string stops and the next one starts?

## Level 2
A separator character fails as soon as a string contains that character. What could the decoder be told in advance instead, so it never has to look inside a string to find its end?

## Level 3
Tell the decoder how long each string is, and put that number somewhere it can be read without guessing where the number itself ends.
