package pl.AWTGameEngine.objects.lists;

import java.util.ArrayList;
import java.util.List;

/**
 * FloatValues is an object, which extends from {@link ArrayList}.
 * <p>
 * You cannot pass {@link java.util.List} or {@link ArrayList} directly
 * into method annotated as {@link pl.AWTGameEngine.annotations.methods.FromXML} due to
 * Java limitations (<b>type erasure</b>). Instead, use this or similar Values class to
 * receive a {@link ArrayList} directly from XML.
 * </p>
 */
public class FloatValues extends Values<Float> {

    public FloatValues() {
        super();
    }

    public FloatValues(String values) {
        super(values);
    }

    public FloatValues(List<Float> values) {
        super(values);
    }

    /**
     * Used to deserialize string chain into a list.
     * @param values Values in a string chain, e.g. <code>0.5,2.3,1.0,5.4,10.2</code>
     */
    public FloatValues of(String values) {
        String[] split = splitAndClear(values);
        for(String s : split) {
            add(Float.parseFloat(s));
        }
        return this;
    }

}
