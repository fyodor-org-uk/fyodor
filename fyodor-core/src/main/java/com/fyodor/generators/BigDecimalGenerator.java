package com.fyodor.generators;

import com.fyodor.random.RandomValues;
import com.fyodor.range.Range;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;

final class BigDecimalGenerator implements Generator<BigDecimal> {

    private final RandomValues randomValues;
    private final Range<BigDecimal> range;
    private final int scale;

    BigDecimalGenerator(final RandomValues randomValues, final Range<BigDecimal> range, final int scale) {
        this.range = range;
        this.scale = scale;
        this.randomValues = randomValues;
    }

    @Override
    public BigDecimal next() {
        if (range.lowerBound().compareTo(range.upperBound()) == 0) {
            return range.lowerBound();
        }

        final double lowerBound = range.lowerBound().doubleValue();
        final double upperBound = range.upperBound().doubleValue();

        return valueOf(randomValues.randomDouble(lowerBound, upperBound, scale));
    }
}
