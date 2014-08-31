package com.fyodor.collect.generators;

import com.fyodor.generators.Generator;
import com.fyodor.range.Range;

import java.lang.reflect.Array;

import static com.fyodor.random.RandomValuesProvider.randomValues;

final class ArrayGenerator<T> implements Generator<T[]> {

    private final Class<? extends T> classOfT;
    private final Generator<? extends T> generatorOfT;
    private final Range<Integer> sizeRange;

    ArrayGenerator(final Class<? extends T> classOfT, final Generator<? extends T> generatorOfT, final Range<Integer> sizeRange) {
        this.classOfT = classOfT;
        this.generatorOfT = generatorOfT;
        this.sizeRange = sizeRange.limit(Range.closed(0, 1000));
    }

    @Override
    public T[] next() {
        final int size = randomValues().randomInteger(sizeRange.lowerBound(), sizeRange.upperBound());
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
