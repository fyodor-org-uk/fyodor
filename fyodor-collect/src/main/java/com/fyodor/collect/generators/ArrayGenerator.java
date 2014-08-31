package com.fyodor.collect.generators;

import com.fyodor.generators.Generator;
import com.fyodor.random.RandomValues;
import com.fyodor.range.Range;

import java.lang.reflect.Array;

final class ArrayGenerator<T> implements Generator<T[]> {

    private final RandomValues randomValues;
    private final Class<? extends T> classOfT;
    private final Generator<? extends T> generatorOfT;
    private final Range<Integer> sizeRange;

    ArrayGenerator(final RandomValues randomValues, final Class<? extends T> classOfT, final Generator<? extends T> generatorOfT, final Range<Integer> sizeRange) {
        this.randomValues = randomValues;
        this.classOfT = classOfT;
        this.generatorOfT = generatorOfT;
        this.sizeRange = sizeRange.limit(Range.closed(0, 1000));
    }

    @Override
    public T[] next() {
        final int size = randomValues.randomInteger(sizeRange.lowerBound(), sizeRange.upperBound());
        final T[] arrayOfT = newArray(classOfT, size);

        for (int i = 0; i < size; i++) {
            arrayOfT[i] = generatorOfT.next();
        }

        return arrayOfT;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] newArray(final Class<? extends T> classOfT, final int size) {
        return (T[]) Array.newInstance(classOfT, size);
    }
}
