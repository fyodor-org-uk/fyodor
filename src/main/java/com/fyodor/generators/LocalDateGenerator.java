package com.fyodor.generators;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.joda.time.LocalDate;

final class LocalDateGenerator implements Generator<LocalDate> {

    private static final LocalDate LOWER_BOUND = new LocalDate(0, 1, 1);
    private static final LocalDate UPPER_BOUND = new LocalDate(2999, 12, 31);

    private final RandomValues randomValues;
    private final LocalDateRange range;

    LocalDateGenerator(final RandomValues randomValues, final Range<LocalDate> range) {
        this.randomValues = randomValues;
        this.range = new LocalDateRange(range.intersection(Range.closed(LOWER_BOUND, UPPER_BOUND)));
    }

    @Override
    public LocalDate next() {
        final long lowerBound = range.lowerEndpoint().toDateTimeAtStartOfDay().toInstant().getMillis();
        final long upperBound = range.upperEndpoint().plusDays(1).toDateTimeAtStartOfDay().toInstant().getMillis();
        return new LocalDate(randomValues.randomLong(lowerBound, upperBound));
    }

    private static final class LocalDateRange {

        private final Range<LocalDate> range;

        private LocalDateRange(final Range<LocalDate> range) {
            this.range = range;
        }

        private LocalDate lowerEndpoint() {
            final LocalDate lowerEndpoint = range.lowerBoundType() == BoundType.CLOSED
                    ? range.lowerEndpoint()
                    : range.lowerEndpoint().plusDays(1);

            return lowerEndpoint;
        }

        private LocalDate upperEndpoint() {
            final LocalDate upperEndpoint = range.upperBoundType() == BoundType.CLOSED
                    ? range.upperEndpoint()
                    : range.upperEndpoint().minusDays(1);

            return upperEndpoint;
        }
    }
}
