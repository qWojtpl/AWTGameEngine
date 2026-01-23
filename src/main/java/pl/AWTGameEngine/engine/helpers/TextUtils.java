package pl.AWTGameEngine.engine.helpers;

public class TextUtils {

    public static String getSpaces(String word, int target) {
        StringBuilder builder = new StringBuilder();
        for(int i = word.length(); i < target; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    public static String wrap(String text, int target, int spaces, String prefix) {
        StringBuilder builder = new StringBuilder();
        int iterations = text.length() / target + 1;
        if(text.length() == target) {
            iterations = 1;
        }
        for(int i = 0; i < iterations; i++) {
            if(i != 0) {
                builder.append("\n");
                builder.append(prefix);
                builder.append(getSpaces("", spaces));
            }
            StringBuilder wordBuilder = new StringBuilder();
            for(int j = i * target; j < text.length() && j < (i + 1) * target; j++) {
                wordBuilder.append(text.charAt(j));
            }
            builder.append(wordBuilder);
        }
        return builder.toString();
    }

}
