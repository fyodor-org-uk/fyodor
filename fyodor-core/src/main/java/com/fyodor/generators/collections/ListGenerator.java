package com.fyodor.generators.collections;

import com.fyodor.generators.Generator;
import com.fyodor.random.RandomValues;
import com.fyodor.range.Range;

import java.util.LinkedList;
import java.util.List;

import static com.fyodor.range.Range.closed;

final class ListGenerator<T> implements Generator<List<T>> {

    private final RandomValues randomValues;
    private final Generator<? extends T> generatorOfT;
    private final Range<Integer> sizeRange;

    ListGenerator(final RandomValues randomValues, final Generator<? extends T> generatorOfT, final Range<Integer> sizeRange) {
        this.randomValues = randomValues;
        this.generatorOfT = generatorOfT;
        this.sizeRange = sizeRange.limit(closed(0, 1000));
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
