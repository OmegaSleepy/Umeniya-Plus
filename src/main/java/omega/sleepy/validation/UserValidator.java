package omega.sleepy.validation;

public class UserValidator {

    public static final int MIN_ALPHANUMERICAL = 6;
    public static final int MIN_SPECIAL = 2;
    public static final int MIN_NUMBER = 2;
    public static final int MIN_LENGTH = 12;

    public static boolean isPasswordFormatValid(String password) {
        if (password == null || password.length() < MIN_LENGTH) return false;

        int letters = 0;
        int digits = 0;
        int specials = 0;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                letters++;
            } else if (Character.isDigit(c)) {
                digits++;
            } else {
                specials++;
            }
        }

        return letters >= MIN_ALPHANUMERICAL &&
                digits >= MIN_NUMBER &&
                specials >= MIN_SPECIAL;
    }

}
