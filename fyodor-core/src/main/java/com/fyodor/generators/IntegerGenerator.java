package com.fyodor.generators;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import static com.fyodor.generators.RandomValuesProvider.randomValues;

class IntegerGenerator implements Generator<Integer> {

    private final Integer min;
    private final Integer max;

    public IntegerGenerator(Integer max) {
        this.max = max;
        this.min = 0;
    }

    public IntegerGenerator(Range<Integer> range) {
        this.min = range.lowerBoundType().equals(BoundType.CLOSED) ?
                range.lowerEndpoint() :
                range.lowerEndpoint() + 1;

        this.max = range.upperBoundType().equals(BoundType.CLOSED) ?
                range.upperEndpoint() :
                range.upperEndpoint() - 1;
    }

    @Override
    public Integer next() {
        return randomValues().randomInteger(min, max);
    }
}
