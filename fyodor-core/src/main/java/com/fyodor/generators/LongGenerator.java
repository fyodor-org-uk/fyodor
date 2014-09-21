package com.fyodor.generators;

import com.fyodor.random.RandomValues;
import com.fyodor.range.Range;

final class LongGenerator implements Generator<Long> {

    private final RandomValues randomValues;
    private final Range<Long> range;

    LongGenerator(final RandomValues randomValues, final Range<Long> range) {
        this.randomValues = randomValues;
        this.range = range;
    }

    @Override
    public Long next() {
        return randomValues.randomLong(range.lowerBound(), range.upperBound());
    }
}
