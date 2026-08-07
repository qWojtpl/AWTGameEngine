package pl.AWTGameEngine.objects.lists;

import java.util.ArrayList;
import java.util.List;

/**
 * IntegerValues is an object, which extends from {@link ArrayList}.
 * <p>
 * You cannot pass {@link java.util.List} or {@link ArrayList} directly
 * into method annotated as {@link pl.AWTGameEngine.annotations.methods.FromXML} due to
 * Java limitations (<b>type erasure</b>). Instead, use this or similar Values class to
 * receive a {@link ArrayList} directly from XML.
 * </p>
 */
public class IntegerValues extends Values<Integer> {

    public IntegerValues() {
        super();
    }

    public IntegerValues(String values) {
        super(values);
    }

    public IntegerValues(List<Integer> values) {
        super(values);
    }

    /**
     * Used to deserialize string chain into a list.
     * @param values Values in a string chain, e.g. <code>2,10,4,7,1,0</code>
     */
    public IntegerValues of(String values) {
        String[] split = splitAndClear(values);
        for(String s : split) {
            add(Integer.parseInt(s));
        }
        return this;
    }

}
