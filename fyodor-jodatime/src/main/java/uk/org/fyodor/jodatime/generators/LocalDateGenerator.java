package uk.org.fyodor.jodatime.generators;

import org.joda.time.LocalDate;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.jodatime.range.LocalDateRange;
import uk.org.fyodor.random.RandomValues;

final class LocalDateGenerator implements Generator<LocalDate> {

    private final RandomValues randomValues;
    private final LocalDateRange range;

    LocalDateGenerator(final RandomValues randomValues, final LocalDateRange range) {
        this.randomValues = randomValues;
        this.range = range.limit();
    }

    @Override
    public LocalDate next() {
        final long lowerBound = range.lowerBound().toDateTimeAtStartOfDay().toInstant().getMillis();
        final long upperBound = range.upperBound().plusDays(1).toDateTimeAtStartOfDay().toInstant().getMillis();
        return new LocalDate(randomValues.randomLong(lowerBound, upperBound));
    }
}
