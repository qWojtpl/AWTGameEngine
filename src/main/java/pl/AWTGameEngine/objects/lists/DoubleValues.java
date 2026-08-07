package pl.AWTGameEngine.objects.lists;

import java.util.ArrayList;
import java.util.List;

/**
 * DoubleValues is an object, which extends from {@link ArrayList}.
 * <p>
 * You cannot pass {@link List} or {@link ArrayList} directly
 * into method annotated as {@link pl.AWTGameEngine.annotations.methods.FromXML} due to
 * Java limitations (<b>type erasure</b>). Instead, use this or similar Values class to
 * receive a {@link ArrayList} directly from XML.
 * </p>
 */
public class DoubleValues extends Values<Double> {

    public DoubleValues() {
        super();
    }

    public DoubleValues(String values) {
        super(values);
    }

    public DoubleValues(List<Double> values) {
        super(values);
    }

    /**
     * Used to deserialize string chain into a list.
     * @param values Values in a string chain, e.g. <code>0.5,2.3,1.0,5.4,10.2</code>
     */
    public DoubleValues of(String values) {
        String[] split = splitAndClear(values);
        for(String s : split) {
            add(Double.parseDouble(s));
        }
        return this;
    }

}
