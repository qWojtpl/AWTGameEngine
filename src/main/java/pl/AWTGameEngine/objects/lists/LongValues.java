package pl.AWTGameEngine.objects.lists;

import java.util.ArrayList;
import java.util.List;

/**
 * LongValues is an object, which extends from {@link ArrayList}.
 * <p>
 * You cannot pass {@link List} or {@link ArrayList} directly
 * into method annotated as {@link pl.AWTGameEngine.annotations.methods.FromXML} due to
 * Java limitations (<b>type erasure</b>). Instead, use this or similar Values class to
 * receive a {@link ArrayList} directly from XML.
 * </p>
 */
public class LongValues extends Values<Long> {

    public LongValues() {
        super();
    }

    public LongValues(String values) {
        super(values);
    }

    public LongValues(List<Long> values) {
        super(values);
    }

    /**
     * Used to deserialize string chain into a list.
     * @param values Values in a string chain, e.g. <code>2,10,4,7,1,0</code>
     */
    public LongValues of(String values) {
        String[] split = splitAndClear(values);
        for(String s : split) {
            add(Long.parseLong(s));
        }
        return this;
    }

}
