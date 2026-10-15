## Level 1
When a closing bracket arrives, which opening bracket must it match? Think about which one was opened most recently and is still waiting.

## Level 2
You always need the latest unmatched opener, and once it is matched it is gone and the one before it becomes the latest. Which data structure gives you that?

## Level 3
Scan the string with a stack. Push each opening bracket. For a closing bracket, fail if the stack is empty or its top is a different kind; otherwise pop. At the end the string is balanced only if the stack is empty.
