package com.fyodor.generators;

import java.util.ArrayList;
import java.util.List;

import static com.fyodor.generators.RandomValuesProvider.randomValues;

final class ValueGenerator<T> implements Generator<T> {

    private final List<T> listOfTs;

    ValueGenerator(final Iterable<T> iterableOfTs) {
        this.listOfTs = new ArrayList<T>();
        for (final T t : iterableOfTs) {
            this.listOfTs.add(t);
        }
    }

    @Override
    public T next() {
        final int index = randomValues().randomInteger(0, listOfTs.size() - 1);
        return listOfTs.get(index);
    }
}
