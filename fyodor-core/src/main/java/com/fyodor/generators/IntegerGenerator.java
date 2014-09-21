package com.fyodor.generators;

import com.fyodor.random.RandomValues;
import com.fyodor.range.Range;

final class IntegerGenerator implements Generator<Integer> {

    private final RandomValues randomValues;
    private final Range<Integer> range;

    IntegerGenerator(final RandomValues randomValues, final Range<Integer> range) {
        this.randomValues = randomValues;
        this.range = range;
    }

    @Override
    public Integer next() {
        return randomValues.randomInteger(range.lowerBound(), range.upperBound());
    }
}
