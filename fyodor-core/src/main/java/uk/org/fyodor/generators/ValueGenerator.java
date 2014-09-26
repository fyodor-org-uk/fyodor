package uk.org.fyodor.generators;

import uk.org.fyodor.random.RandomValues;

import java.util.ArrayList;
import java.util.List;

final class ValueGenerator<T> implements Generator<T> {

    private final RandomValues randomValues;
    private final List<T> listOfTs;

    ValueGenerator(final RandomValues randomValues, final Iterable<T> iterableOfTs) {
        this.randomValues = randomValues;
        this.listOfTs = new ArrayList<T>();
        for (final T t : iterableOfTs) {
            this.listOfTs.add(t);
        }
    }

    @Override
    public T next() {
        final int index = randomValues.randomInteger(0, listOfTs.size() - 1);
        return listOfTs.get(index);
    }
}
