package pl.AWTGameEngine.objects.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Values<T> extends ArrayList<T> {

    public Values() {

    }

    public Values(String values) {
        of(values);
    }

    public Values(List<T> values) {
        addAll(values);
    }

    public abstract Values<T> of(String values);

    protected String[] splitAndClear(String values) {
        if(values.isEmpty()) {
            return new String[0];
        }
        clear();
        return values.trim().split("\\s*,\\s*");
    }

    @Override
    public String toString() {
        return stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

}
