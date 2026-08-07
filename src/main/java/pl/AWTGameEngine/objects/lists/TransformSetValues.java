package pl.AWTGameEngine.objects.lists;

import pl.AWTGameEngine.objects.transform.TransformSet;

import java.util.ArrayList;
import java.util.List;

/**
 * TransformSetValues is an object, which extends from {@link ArrayList}.
 * <p>
 * You cannot pass {@link List} or {@link ArrayList} directly
 * into method annotated as {@link pl.AWTGameEngine.annotations.methods.FromXML} due to
 * Java limitations (<b>type erasure</b>). Instead, use this or similar Values class to
 * receive a {@link ArrayList} directly from XML.
 * </p>
 */
public class TransformSetValues extends Values<TransformSet> {

    public TransformSetValues() {
        super();
    }

    public TransformSetValues(String values) {
        super(values);
    }

    public TransformSetValues(List<TransformSet> values) {
        super(values);
    }

    @Override
    public TransformSetValues of(String values) {
        String[] split = splitAndClear(values);
        double x = 0, y = 0, z;
        for(int i = 0, j = 0; i < split.length; i++, j++) {
            if(j == 0) {
                x = Double.parseDouble(split[i]);
            } else if(j == 1) {
                y = Double.parseDouble(split[i]);
            } else if(j == 2) {
                z = Double.parseDouble(split[i]);
                add(new TransformSet(x, y, z));
                j = -1;
            }
        }
        return this;
    }

}
