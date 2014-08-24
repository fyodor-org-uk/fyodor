package com.fyodor.jodatime.generators;

import com.fyodor.generators.Generator;
import com.fyodor.generators.RandomValues;
import com.fyodor.range.Range;
import org.joda.time.LocalDate;

final class LocalDateGenerator implements Generator<LocalDate> {

    private static final LocalDate LOWER_BOUND = new LocalDate(0, 1, 1);
    private static final LocalDate UPPER_BOUND = new LocalDate(2999, 12, 31);

    private final RandomValues randomValues;
    private final Range<LocalDate> range;

    LocalDateGenerator(final RandomValues randomValues, final Range<LocalDate> range) {
        this.randomValues = randomValues;
        this.range = saneRange(range);
    }

    @Override
    public LocalDate next() {
        final long lowerBound = range.lowerBound().toDateTimeAtStartOfDay().toInstant().getMillis();
        final long upperBound = range.upperBound().plusDays(1).toDateTimeAtStartOfDay().toInstant().getMillis();
        return new LocalDate(randomValues.randomLong(lowerBound, upperBound));
    }

    private Range<LocalDate> saneRange(Range<LocalDate> range) {
        return Range.closed(range.lowerBound().isBefore(LOWER_BOUND) ? LOWER_BOUND : range.lowerBound(),
                range.upperBound().isAfter(UPPER_BOUND) ? UPPER_BOUND : range.upperBound());
    }
}
