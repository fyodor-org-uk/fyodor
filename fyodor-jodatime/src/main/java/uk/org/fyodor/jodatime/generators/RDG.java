package uk.org.fyodor.jodatime.generators;

import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.range.Range;
import org.joda.time.LocalDate;

import static uk.org.fyodor.random.RandomValuesProvider.randomValues;

public final class RDG {

    public static Generator<LocalDate> localDate(final Range<LocalDate> range) {
        if (range == null) {
            throw new IllegalArgumentException("range cannot be null");
        }

        return new LocalDateGenerator(randomValues(), range);
    }

    private RDG() {
    }
}
