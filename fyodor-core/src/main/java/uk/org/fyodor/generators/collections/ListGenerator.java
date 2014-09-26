package uk.org.fyodor.generators.collections;

import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.random.RandomValues;
import uk.org.fyodor.range.Range;

import java.util.LinkedList;
import java.util.List;

public final class ListGenerator<T> implements Generator<List<T>> {

    private final RandomValues randomValues;
    private final Generator<? extends T> generatorOfT;
    private final Range<Integer> sizeRange;

    public ListGenerator(final RandomValues randomValues, final Generator<? extends T> generatorOfT, final Range<Integer> sizeRange) {
        this.randomValues = randomValues;
        this.generatorOfT = generatorOfT;
        this.sizeRange = sizeRange.limit(Range.closed(0, 1000));
    }

    @Override
    public List<T> next() {
        final int size = randomValues.randomInteger(sizeRange.lowerBound(), sizeRange.upperBound());
        final List<T> listOfTs = new LinkedList<T>();
        for (int i = 0; i < size; i++) {
            listOfTs.add(generatorOfT.next());
        }
        return listOfTs;
    }
}
