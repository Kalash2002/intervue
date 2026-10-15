/** Decides whether a phrase is a palindrome when case and punctuation are ignored. */
public class Palindromes {

    /** Returns true when the letters and digits of s read the same in both directions. */
    static boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length() - 1;
        while (left < right) {
            if (!Character.isLetterOrDigit(s.charAt(left))) {
                left++; // ignore this character on the left
            } else if (!Character.isLetterOrDigit(s.charAt(right))) {
                right--; // ignore this one on the right
            } else if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false; // a pair that counts and differs
            } else {
                left++;
                right--;
            }
        }
        return true; // the positions met: every pair matched
    }
}
