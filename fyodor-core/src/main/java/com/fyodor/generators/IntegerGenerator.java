package com.fyodor.generators;

import com.fyodor.range.Range;

import static com.fyodor.random.RandomValuesProvider.randomValues;

class IntegerGenerator implements Generator<Integer> {

    private final Integer min;
    private final Integer max;

    IntegerGenerator(Integer max) {
        this.max = max;
        this.min = 0;
    }

    IntegerGenerator(Range<Integer> range) {
        this.min = range.lowerBound();

        this.max = range.upperBound();
    }

    @Override
    public Integer next() {
        return randomValues().randomInteger(min, max);
    }
}
