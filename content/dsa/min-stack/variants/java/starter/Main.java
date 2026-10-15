public class Main {
    public static void main(String[] args) {
        MinStack stack = new MinStack();
        stack.push(4);
        stack.push(7);
        System.out.println(stack.getMin() + " " + stack.top());
        stack.pop();
        System.out.println(stack.top());
    }
}
