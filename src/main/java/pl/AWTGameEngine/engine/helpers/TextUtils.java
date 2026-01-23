package pl.AWTGameEngine.engine.helpers;

public class TextUtils {

    public static String getSpaces(String word, int target) {
        StringBuilder builder = new StringBuilder();
        for(int i = word.length(); i < target; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

}
