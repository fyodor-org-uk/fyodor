package com.fyodor.generators;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

class IntegerGenerator implements Generator<Integer> {

    private final Integer min;
    private final Integer max;

    public IntegerGenerator(Integer max) {
        this.max = max;
        this.min = 0;
    }

    public IntegerGenerator(Range<Integer> range) {
        this.min = range.lowerBoundType().equals(BoundType.OPEN) ?
                range.lowerEndpoint() + 1 :
                range.lowerEndpoint();

        this.max = range.upperBoundType().equals(BoundType.CLOSED) ?
                range.upperEndpoint() + 1 :
                range.upperEndpoint();
    }

    @Override
    public Integer next() {
        return RDG.random.nextInt(max - min) + min;
    }
}
