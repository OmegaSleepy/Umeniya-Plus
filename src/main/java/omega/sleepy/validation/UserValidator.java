package omega.sleepy.validation;

import omega.sleepy.util.Log;

public class UserValidator {

    public static final int MIN_ALPHANUMERICAL = 6;
    public static final int MIN_SPECIAL = 1;
    public static final int MIN_NUMBER = 2;
    public static final int MIN_LENGTH = 12;

    public static boolean isPasswordFormatInvalid(String password) {
        if (password == null || password.length() < MIN_LENGTH) return true;

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
        //TODO return those as throwable
        if(letters < MIN_ALPHANUMERICAL) Log.error("Too little alphanumerical");
        if(digits < MIN_NUMBER) Log.error("Too little digits");
        if(specials < MIN_SPECIAL) Log.error("Too little special characters");

        return letters < MIN_ALPHANUMERICAL ||
                digits < MIN_NUMBER ||
                specials < MIN_SPECIAL;
    }

}
