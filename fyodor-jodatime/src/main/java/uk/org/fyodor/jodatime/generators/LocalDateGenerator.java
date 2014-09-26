package uk.org.fyodor.jodatime.generators;

import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.random.RandomValues;
import uk.org.fyodor.range.Range;
import org.joda.time.LocalDate;

import static uk.org.fyodor.range.Range.closed;

final class LocalDateGenerator implements Generator<LocalDate> {

    private static final LocalDate LOWER_BOUND = new LocalDate(0, 1, 1);
    private static final LocalDate UPPER_BOUND = new LocalDate(2999, 12, 31);

    private final RandomValues randomValues;
    private final Range<LocalDate> range;

    LocalDateGenerator(final RandomValues randomValues, final Range<LocalDate> range) {
        this.randomValues = randomValues;
        this.range = range.limit(closed(LOWER_BOUND, UPPER_BOUND));
    }

    @Override
    public LocalDate next() {
        final long lowerBound = range.lowerBound().toDateTimeAtStartOfDay().toInstant().getMillis();
        final long upperBound = range.upperBound().plusDays(1).toDateTimeAtStartOfDay().toInstant().getMillis();
        return new LocalDate(randomValues.randomLong(lowerBound, upperBound));
    }
}
