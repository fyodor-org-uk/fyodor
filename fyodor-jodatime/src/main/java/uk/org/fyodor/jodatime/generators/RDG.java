package uk.org.fyodor.jodatime.generators;

import org.joda.time.LocalDate;
import uk.org.fyodor.generators.Generator;
import uk.org.fyodor.jodatime.range.LocalDateRange;

import static uk.org.fyodor.generators.Validations.ensure;
import static uk.org.fyodor.generators.Validations.isNotNull;
import static uk.org.fyodor.jodatime.range.LocalDateRange.all;
import static uk.org.fyodor.random.RandomValuesProvider.randomValues;

public class RDG {

    public static Generator<LocalDate> localDate() {
        return localDate(all());
    }

    public static Generator<LocalDate> localDate(final LocalDateRange range) {
        ensure(isNotNull(range), "range cannot be null");

        return new LocalDateGenerator(randomValues(), range);
    }

    private RDG() {
    }
}
