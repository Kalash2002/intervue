public class Main {
    public static void main(String[] args) {
        Twitter app = new Twitter();
        app.postTweet(4, 101);
        app.postTweet(9, 205);
        app.follow(4, 9);
        System.out.println(app.getNewsFeed(4));
    }
}
