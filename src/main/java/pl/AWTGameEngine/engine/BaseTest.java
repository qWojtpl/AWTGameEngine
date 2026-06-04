package pl.AWTGameEngine.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BaseTest {

    private final String[] words = new String[]
            {"Cat", "Beaver", "Hops", "Jumps", "Into", "House", "Void", "Number", "Pleasant"};

    public abstract void setup(Object context);
    public abstract boolean perform();
    public abstract void end();

    protected String getRandomFileString() {
        return getRandomString(10).replace(" ", "") + ".txt";
    }

    protected String getRandomString(int numberOfWords) {
        List<String> randomWords = new ArrayList<>();
        for(int i = 0; i < numberOfWords; i++) {
            randomWords.add(words[getRandom(words.length)]);
        }
        return String.join(" ", randomWords);
    }

    private int getRandom(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

}
